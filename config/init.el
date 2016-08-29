
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
 )


(require 'package) ;; You might already have this line
(add-to-list 'package-archives
             '("melpa" . "http://melpa.org/packages/") t)

(package-initialize) ;; You might already have this line

;; cider config

(add-hook 'cider-mode-hook #'eldoc-mode)
(add-hook 'cider-mode-hook #'company-mode)

(setq nrepl-hide-special-buffers t)

;; disable menu bar (but allows popup menus)
(menu-bar-mode -99)

;; disable toolbar
(tool-bar-mode -1)

;; split window horizontally
(setq split-width-threshold 9999)

(require 'live-clojure-talks)
