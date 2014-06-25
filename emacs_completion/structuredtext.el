;;; structuredtext.el --- Support for the Structured Text programming language

;; Author: Simon Mages <mages.simon@googlemail.com>
;; Maintainer: Simon Mages <mages.simon@googlemail.com>
;; Created: 25 Jun 2014
;; Keywords: languages
;; Version: 1.0

;; This file is not part of GNU Emacs

;; Copyright (C) 2014-2015 Haw-Landshut
;;
;; Permission to use, copy, modify, and/or distribute this software for any
;; purpose with or without fee is hereby granted, provided that the above
;; copyright notice and this permission notice appear in all copies.
;;
;; THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
;; WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
;; MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
;; ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
;; WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
;; ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
;; OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

;;; Commentary:
;;
;; the Structured Text Major mode provides basic Syntax highlighting and indentation
;; for Structured Text how it is used in Project Icarus
;;
;;; Code:
(defvar structuredtext-mode-hook nil)

;;;###autoload (magic comment to register this function global but don't load the whole library)
(add-to-list 'auto-mode-alist '("\\.st\\'" . structuredtext-mode))

;; command to comment/uncomment text
(defun structuredtext-comment-dwim (arg) 
  "Comment line or region with the EMACS builtin function 'comment-dwim'.
ARG can be a symbol."
  (interactive "*P")
  (require 'newcomment)
  (let (
        (comment-start "(*") (comment-end "*)")
        )
    (comment-dwim arg)))

;; the keymap overrides for the Structured Text mode
(defvar structuredtext-mode-map
  (let ((map (make-keymap)))
    (define-key map "\C-j" 'newline-and-indent)
    (define-key map [remap comment-dwim] 'structuredtext-comment-dwim)
    map)
  "Keymap for Structured Text major mode.")

;; basic highlighting group
(defconst structuredtext-font-lock-keywords-1
  (list
   '((concat (concat "\\<" (regexp-opt '("PROGRAM" "END_PROGRAM" "FUNCTION" "END_FUNCTION" "FUNCTION_BLOCK" "END_FUNCTION_BLOCK" "VAR" "END_VAR" "VAR_INPUT" "END_VAR_INPUT" "VAR_OUTPUT" "END_VAR_OUTPUT" "VAR_IN_OUT" "END_VAR_IN_OUT" "VAR_CONFIG" "END_VAR_CONFIG" "VAR_GLOBAL" "END_VAR_GLOBAL" "IF" "END_IF" "THEN" "ELSE" "CASE" "END_CASE" "OF" "BY" "TO" "FOR" "END_FOR" "WHILE" "END_WHILE" "REPEAT" "END_REPEAT") t)) "\\>") . font-lock-builtin-face)
   '("\\('w*'\\)" . font-lock-variable-name-face))
  "Minimal highlighting.")

;; extended highlighting group
(defconst structuredtext-font-lock-keywords-2
  (append structuredtext-font-lock-keywords-1
          (list
           '((concat (concat "\\<" (regexp-opt '("BOOL" "SINT" "INT" "DINT" "LINT" "USINT" "UINT" "UDINT" "ULINT" "REAL" "LREAL" "TIME" "DATE" "TIME_OF_DATE" "TOD" "DATE_AND_TIME" "TON" "DT" "STRING" "WSTRING" "BYTE") t)) "\\>") . font-lock-keyword-face)
           '("\\<\\(TRUE\\|FALSE\\)\\>" . font-lock-constant-face)
           '((concat (concat "\\<" (regexp-opt '("XOR" "AND" "NOT" "MOD" "OR" "SIN" "COS" "TAN" "ASIN" "ACOS" "ATAN" "LOG" "EXP" "LN" "SQRT" "PRINT") t)) "\\>") . font-lock-function-name-face)))
  "Additional Keywords to highlight.")

;; default highligting level
(defvar structuredtext-font-lock-keywords structuredtext-font-lock-keywords-2
  "Default highlighting level.")

;; indentation - this function is mostly the same as the one on the emacs wiki
(defun structuredtext-indent-line ()
  "Inden current line as Structured Text code."
  (interactive)
  (beginning-of-line)
  (if (bobp)
      (indent-line-to 0)
    (let ((not-indented t) cur-indent)
      (if (looking-at "^[ \t]*END_")
          (progn
            (save-excursion
              (forward-line -1)
              (setq cur-indent (- (current-indentation) tab-width)))
            (if (< cur-indent 0)
                (setq cur-indent 0)))
        (save-excursion
          (while not-indented
            (forward-line -1)
            (if (looking-at "^[ \t]*END_")
                (progn
                  (setq cur-indent (current-indentation))
                  (setq not-indented nil))
              (if (looking-at (concat "^[ \t]*" (regexp-opt '("PROGRAM" "FUNCTION" "FUNCTION_BLOCK" "VAR" "VAR_INPUT" "VAR_OUTPUT" "VAR_IN_OUT" "VAR_CONFIG" "VAR_GLOBAL" "IF" "THEN" "ELSE" "CASE" "OF" "BY" "TO" "FOR" "WHILE" "REPEAT") t)))
                  (progn
                    (setq cur-indent (+ (current-indentation) tab-width))
                    (setq not-indented nil))
                (if (bobp)
                    (setq not-indented nil)))))))
      (if cur-indent
          (indent-line-to cur-indent)
        (indent-line-to 0)))))

;; syntaxtable for the Structured Text mode to make comments work
(defvar structuredtext-mode-syntax-table
  (let ((st (make-syntax-table)))
    (modify-syntax-entry ?_ "w" st)
    (modify-syntax-entry ?\( ". 1" st)
    (modify-syntax-entry ?\) ". 4" st)
    (modify-syntax-entry ?* ". 23" st)
    st)
  "Syntax table for Structured Text mode.")

;; main function to call on loading the mode
(defun structuredtext-mode ()
  "Major mode for editing Structured Text files."
  (interactive)
  (kill-all-local-variables)
  (set-syntax-table structuredtext-mode-syntax-table)
  (use-local-map structuredtext-mode-map)
  (set (make-local-variable 'font-lock-defaults) '(structuredtext-font-lock-keywords))
  (set (make-local-variable 'indent-line-function) 'structuredtext-indent-line)
  (setq major-mode 'structuredtext-mode)
  (setq mode-name "StructuredText")
  (run-hooks 'structuredtext-mode-hook))

;; make the mode public
(provide 'structuredtext-mode)

;;; structuredtext.el ends here
