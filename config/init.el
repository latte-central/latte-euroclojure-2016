
(add-to-list 'load-path "./config/")

(custom-set-variables
 ;; custom-set-variables was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 '(custom-enabled-themes (quote (leuven)))
 '(custom-safe-themes
   (quote
    ("afc220610bee26945b7c750b0cca03775a8b73c27fdca81a586a0a62d45bbce2" default)))
 '(inhibit-startup-screen t)
 '(show-paren-mode t)
 )

(custom-set-faces
 ;; custom-set-faces was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.

 '(live-clojure-talks-title-face
   ((t
     (:height 2.0
      :foreground "slate blue"))))

 '(live-clojure-talks-subtitle-face
   ((t
     (:height 1.5
      :foreground "medium slate blue"))))

'(live-clojure-talks-subsubtitle-face
   ((t
     (:height 1.0
      :foreground "light slate blue"))))

'(live-clojure-talks-button-face
  ((t
    (:slant italic :foreground "IndianRed1" :inherit
	    (custom-button)))))

 )

(require 'package) ;; You might already have this line
(add-to-list 'package-archives
             '("melpa" . "http://melpa.org/packages/") t)

(package-initialize) ;; You might already have this line

;; cider config

(add-hook 'cider-mode-hook #'eldoc-mode)
(add-hook 'cider-mode-hook #'company-mode)

(setq nrepl-hide-special-buffers t)

(setq cider-repl-display-help-banner nil)

;; (setq cider-eval-result-prefix ";; => ")

;; disable menu bar (but allows popup menus)
(menu-bar-mode -99)

;; disable toolbar
(tool-bar-mode -1)

;; split window horizontally
(setq split-width-threshold 9999)

(require 'live-clojure-talks)

;; key bindings

(global-set-key (kbd "C-=") 'text-scale-decrease)
(global-set-key (kbd "C-+") 'text-scale-increase)

(global-set-key (kbd "C-&") 'live-clojure-talks-mode)
(global-set-key (kbd "C-œ") 'smartparens-strict-mode)

(global-set-key (kbd "C-z") 'undo)

(use-package smartparens-config
    :ensure smartparens
    :config
    (progn
      (show-smartparens-global-mode t)))

(add-hook 'prog-mode-hook 'turn-on-smartparens-strict-mode)
(add-hook 'markdown-mode-hook 'turn-on-smartparens-strict-mode)

(bind-keys
 :map smartparens-mode-map
 ("C-M-a" . sp-beginning-of-sexp)
 ("C-M-e" . sp-end-of-sexp)

 ("C-<down>" . sp-down-sexp)
 ("C-<up>"   . sp-up-sexp)
 ("M-<down>" . sp-backward-down-sexp)
 ("M-<up>"   . sp-backward-up-sexp)

 ("C-M-f" . sp-forward-sexp)
 ("C-M-b" . sp-backward-sexp)

 ("C-M-n" . sp-next-sexp)
 ("C-M-p" . sp-previous-sexp)

 ("C-S-f" . sp-forward-symbol)
 ("C-S-b" . sp-backward-symbol)

 ("C-<right>" . sp-forward-slurp-sexp)
 ("M-<right>" . sp-forward-barf-sexp)
 ("C-<left>"  . sp-backward-slurp-sexp)
 ("M-<left>"  . sp-backward-barf-sexp)

 ("C-M-t" . sp-transpose-sexp)
 ("C-M-k" . sp-kill-sexp)
 ("C-k"   . sp-kill-hybrid-sexp)
 ("M-k"   . sp-backward-kill-sexp)
 ("C-M-w" . sp-copy-sexp)

 ("C-M-d" . delete-sexp)

 ("M-<backspace>" . backward-kill-word)
 ("C-<backspace>" . sp-backward-kill-word)
 ([remap sp-backward-kill-word] . backward-kill-word)

 ("M-[" . sp-backward-unwrap-sexp)
 ("M-]" . sp-unwrap-sexp)

 ("C-x C-t" . sp-transpose-hybrid-sexp)

 ("C-c ("  . wrap-with-parens)
 ("C-c ["  . wrap-with-brackets)
 ("C-c {"  . wrap-with-braces)
 ("C-c '"  . wrap-with-single-quotes)
 ("C-c \"" . wrap-with-double-quotes)
 ("C-c _"  . wrap-with-underscores)
 ("C-c `"  . wrap-with-back-quotes))

;; show the time
(display-time-mode 1)

(defun my-minibuffer-setup ()
       (set (make-local-variable 'face-remapping-alist)
          '((default :height 2.0))))
(add-hook 'minibuffer-setup-hook 'my-minibuffer-setup)

(set-face-attribute 'default nil :height 200)
(set-face-attribute 'mode-line nil :height 100)
(set-face-attribute 'window-divider nil :height 100)
(set-face-attribute 'minibuffer-prompt nil :height 100)
(set-face-attribute 'header-line nil :height 100)
(set-face-attribute 'menu nil :height 100)
(set-face-attribute 'button nil :height 100)

;; ido mode

(setq ido-enable-flex-matching t)
(setq ido-everywhere t)
(ido-mode 1)
