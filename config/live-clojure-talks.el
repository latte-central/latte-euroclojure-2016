
;;; live-clojure-talks.el --- (C) 2016 Frederic Peschanski  under the GPL 3

;;; ... a (friendly) fork of  live-code-talks.el

;;; live-code-talks.el --- Support for slides with live code in them  -*- lexical-binding: t; -*-

;; Copyright (C) 2015 David Raymond Christiansen

;; Author: David Raymond Christiansen <david@davidchristiansen.dk>
;; Keywords: docs, multimedia
;; Package-Requires: ((emacs "24") (cl-lib "0.5") (narrowed-page-navigation "0.1"))
;; Version: 0.2.1

;; This program is free software; you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

;;; Commentary:

;; This package provides a minor mode for formatting an Emacs buffer
;; as slides. This package relies on `narrowed-page-navigation-mode'
;; to actually navigate from slide to slide, and instead provides
;; syntax for comments that are rendered as slide elements.
;;
;; The syntax comes pre-configured for Idris or Haskell. For other
;; languages, set `live-code-talks-title-regexp',
;; `live-code-talks-image-regexp', and
;; `live-code-talks-comment-regexp', preferably as file variables.
;; For your presentation, consider also overriding
;; `face-remapping-alist' to get the proper fonts for your screen.
;;; Code:

(require 'cl-lib)
(require 'linum)
(require 'narrowed-page-navigation)

(defgroup live-clojure-talks ()
  "Settings for live code talks"
  :group 'multimedia)

(defface live-clojure-talks-title-face
  '((t (:height 2.0)))
  "Face for showing slide titles"
  :group 'live-clojure-talks)

(defface live-clojure-talks-subtitle-face
  '((t (:inherit live-clojure-talks-title-face)
       (:height 0.75)))
  "Face for showing slide titles"
  :group 'live-clojure-talks)

(defface live-clojure-talks-subsubtitle-face
  '((t (:inherit live-clojure-talks-subtitle-face
                 :height 0.9)))
  "Face for showing slide titles"
  :group 'live-clojure-talks)

(defface live-clojure-talks-button-face
  '((t (:inherit custom-button
                 :slant normal
                 :foreground "black")))
  "Face for clickable buttons in presentations"
  :group 'live-clojure-talks)

(defvar live-clojure-talks-title-regexp "^\\s-*;;;\\s-*#\\s-*\\([^#].*\\)$"
  "The regexp to match for slide titles.  The contents of match group 1 will be highlighted.")
(make-variable-buffer-local 'live-clojure-talks-title-regexp)

(defvar live-clojure-talks-subtitle-regexp "^\\s-*;;;\\s-*##\\s-*\\([^#].*\\)$"
  "The regexp to match for slide subtitles.  The contents of match group 1 will be highlighted.")
(make-variable-buffer-local 'live-clojure-talks-title-regexp)

(defvar live-clojure-talks-subsubtitle-regexp "^\\s-*;;;\\s-*###\\s-*\\([^#].*\\)$"
  "The regexp to match for slide subsubtitles.  The contents of match group 1 will be highlighted.")
(make-variable-buffer-local 'live-clojure-talks-title-regexp)

(defun live-clojure-talks-highlight-titles (regexp face &optional buffer)
  "Use REGEXP to find titles, and highlight them with FACE, placing highlighting on all titles in BUFFER, or the current buffer if nil.

To change the format used for titles, set `live-clojure-talks-title-regexp'."
  (with-current-buffer (or buffer (current-buffer))
    (save-restriction
      (widen)
      (save-excursion
        (goto-char (point-min))
        (while (re-search-forward regexp nil t)
          ;; First make an overlay applying the title face to the
          ;; actual title, in match group 1
          (let ((title-overlay (make-overlay (match-beginning 1) (match-end 1)))
                (title-area-overlay (make-overlay (match-beginning 0) (match-end 0))))
            (overlay-put title-overlay 'live-clojure-talks 'title)
            (overlay-put title-overlay 'face            face)
            (overlay-put title-overlay 'display         t)
            (overlay-put title-area-overlay 'live-clojure-talks 'title)
            (overlay-put title-area-overlay 'display         "")))))))

(defun live-clojure-talks-unhighlight (what &optional buffer)
  "Delete all WHAT highlighting in BUFFER, or the current buffer if nil.

 WHAT can be `title', `image', `comment', `button' or `hidden'."
  (with-current-buffer (or buffer (current-buffer))
    (save-restriction
      (widen)
      (save-excursion
        (let ((overlays (overlays-in (point-min) (point-max))))
          (cl-loop for overlay in overlays
                   when (eq (overlay-get overlay 'live-clojure-talks) what)
                   do (delete-overlay overlay)))))))

(defvar live-clojure-talks-image-regexp "^\\s-*;;;\\s-*\\[\\[\\[\\([^]]+\\)\\]\\]\\]\\s-*$"
  "A regexp to determine which images should be shown.  Group 1 should be an image specification, which will be made relative to the current buffer.")

(defun live-clojure-talks-make-image-relative (image dir)
  "If the specifier IMAGE is a relative filename, return a new specifier with an absolute name relative to DIR.  Otherwise, return IMAGE. Additionally, make any width and height specifications that are floating point numbers into window-relative values."
  (if (not (and (consp image) (eq (car image) 'image)))
      (error "Not an image descriptor")
    (let ((props (cdr image))
          (new-props (cl-copy-list (cdr image))))
      (let ((file-name (plist-get props :file)))
        (when (stringp file-name)
          (setq new-props (plist-put new-props :file (expand-file-name file-name dir)))))
      (let ((width (plist-get props :width)))
        (when (floatp width)
          (setq new-props (plist-put new-props
                                     :width (floor (* (window-pixel-width) width))))))
      (let ((height (plist-get props :height)))
        (when (floatp height)
          (setq new-props (plist-put new-props
                                     :height (floor (* (window-pixel-height) height))))))
      (let ((max-width (plist-get props :max-width)))
        (when (floatp max-width)
          (setq new-props (plist-put new-props
                                     :max-width (floor (* (window-pixel-width) max-width))))))
      (let ((max-height (plist-get props :max-height)))
        (when (floatp max-height)
          (setq new-props (plist-put new-props
                                     :max-height (floor (* (window-pixel-height) max-height))))))
      (cons 'image new-props))))

(defun live-clojure-talks-show-images (&optional buffer)
  "Replace images matching `live-clojure-talks-image-regexp' with the actual image in BUFFER, or the current buffer if nil."
  (with-current-buffer (or buffer (current-buffer))
    (save-restriction
      (widen)
      (save-excursion
        (goto-char (point-min))
        (while (re-search-forward live-clojure-talks-image-regexp nil t)
          (let* ((base-image (read (match-string 1)))
                 (image (live-clojure-talks-make-image-relative base-image (file-name-directory (buffer-file-name))))
                 (image-overlay (make-overlay (match-beginning 0) (match-end 0))))
            (overlay-put image-overlay 'live-clojure-talks 'image)
            (overlay-put image-overlay 'display         image)
            (overlay-put image-overlay 'intangible      'image)))))))

(defface live-clojure-talks-comment-face
  '((t (:inherit default)))
  "Face used for stripped-out comments"
  :group 'live-clojure-talks)

(defvar live-clojure-talks-comment-regexp "^ *;;;\\( *[^[ #{].*\\| *\\)$"
  "The regexp to match for slide titles.  The contents of match group 1 will be highlighted.")
(make-variable-buffer-local 'live-clojure-talks-comment-regexp)

(defun live-clojure-talks-highlight-comments (&optional buffer)
  "Place highlighting on normal comments in BUFFER, or the current buffer if nil.

To change the format used for comments, set `live-clojure-talks-comment-regexp'."
  (with-current-buffer (or buffer (current-buffer))
    (save-restriction
      (widen)
      (save-excursion
        (goto-char (point-min))
        (while (re-search-forward live-clojure-talks-comment-regexp nil t)
          ;; First make an overlay applying the comment face to the
          ;; actual comment, in match group 1
          (let ((comment-overlay (make-overlay (match-beginning 1) (match-end 1)))
                (comment-area-overlay (make-overlay (match-beginning 0) (match-end 0))))
            (overlay-put comment-overlay 'live-clojure-talks 'comment)
            (overlay-put comment-overlay 'face            'live-clojure-talks-comment-face)
            (overlay-put comment-overlay 'display         t)
            (overlay-put comment-area-overlay 'read-only       t)
            (overlay-put comment-area-overlay 'live-clojure-talks 'comment)
            (overlay-put comment-area-overlay 'display         "")))))))

(defvar live-clojure-talks-begin-hide-regexp ";;; *{hide} *"
  "Regexp beginning regions that should be invisible in slide mode.")

(defvar live-clojure-talks-end-hide-regexp ";;; *{show} *"
  "Regexp ending regions that should be invisible in slide mode.")

(defun live-clojure-talks-hide-junk (&optional buffer)
  "Don't display hidden regions in BUFFER, or current buffer if nil."
  (with-current-buffer (or buffer (current-buffer))
    (save-restriction
      (widen)
      (save-excursion
        (goto-char (point-min))
        (let (hide-start hide-end overlay)
          (while (re-search-forward live-clojure-talks-begin-hide-regexp nil t)
            (setq hide-start (match-beginning 0))
            (when (re-search-forward live-clojure-talks-end-hide-regexp nil t)
              (setq hide-end (match-end 0))
              (setq overlay (make-overlay hide-start hide-end))
              (overlay-put overlay 'live-clojure-talks 'hidden)
              (overlay-put overlay 'display "")
              (overlay-put overlay 'priority 10))))))))

(defun live-clojure-talks-in-comment-p (&optional pos)
  "Determine whether POS is in a comment or not."
  (save-excursion (nth 4 (syntax-ppss pos))))

(defvar live-clojure-talks-button-regexp "<<<\\(.+\\)|||\\(.+\\)>>>"
  "Regexp describing how to find clickable buttons. Matching
group 1 contains the button text and matching group 2 contains
the Lisp expresion to evaluate.")

(defun live-clojure-talks-make-buttons (&optional buffer)
  (with-current-buffer (or buffer (current-buffer))
    (save-restriction
      (widen)
      (save-excursion
        (goto-char (point-min))
        (while (re-search-forward live-clojure-talks-button-regexp nil t)
          (when (and (live-clojure-talks-in-comment-p (match-beginning 0))
                     (live-clojure-talks-in-comment-p (match-end 0)))
            (let* ((text (match-string 1))
                   (expr (match-string 2))
                   (button (make-button (match-beginning 0) (match-end 0)
                                        'action (read expr)
                                        'display (progn (set-text-properties 0 (length text) nil text)
                                                        text)
                                        'face 'live-clojure-talks-button-face)))
              (overlay-put button 'priority 10)
              (overlay-put button 'live-clojure-talks 'button))))))))



(defvar live-clojure-talks-restore-linum nil
  "Whether to re-enable linum on exit from slide mode.")
(make-variable-buffer-local 'live-clojure-talks-restore-linum)

;;;###autoload
(define-minor-mode live-clojure-talks-mode
  "A minor mode for presenting a code buffer as slides."
  nil "Talk" nil
  (if live-clojure-talks-mode
      (progn
        (setq live-clojure-talks-restore-linum linum-mode)
        (linum-mode -1)
        (live-clojure-talks-highlight-titles live-clojure-talks-title-regexp
                                          'live-clojure-talks-title-face)
        (live-clojure-talks-highlight-titles live-clojure-talks-subtitle-regexp
                                          'live-clojure-talks-subtitle-face)
        (live-clojure-talks-highlight-titles live-clojure-talks-subsubtitle-regexp
                                          'live-clojure-talks-subsubtitle-face)
        (live-clojure-talks-show-images)
        (live-clojure-talks-hide-junk)
        (live-clojure-talks-highlight-comments)
        (live-clojure-talks-make-buttons)
        (narrow-to-page)
        (narrowed-page-navigation-mode 1))
    (progn
      (when live-clojure-talks-restore-linum (linum-mode 1))
      (widen)
      (narrowed-page-navigation-mode -1)
      (cl-loop for what in '(title image comment hidden button)
               do (live-clojure-talks-unhighlight what)))))


(provide 'live-clojure-talks)
;;; live-clojure-talks.el ends here
