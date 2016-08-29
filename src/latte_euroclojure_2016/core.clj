
;;; # Live-coding Mathematics
;;; ## Your first Clojure proof

;;    using the LaTTe proof assistant: <<<https://github.com/fredokun/LaTTe|||(lambda (x) (browse-url "https://github.com/fredokun/LaTTe"))>>>

         ;;;                  ((((
         ;;;                 ((((
         ;;;                  ))))
         ;;;               _ .---.
         ;;;              ( |`---'|
         ;;;               \|     |
         ;;;               : .___, :
         ;;;                `-----'  -Karl

;;; ### Frédéric Peschanski @ Euroclojure 2016

;;; Associate Professor at UPMC
;; (Université Pierre & Marie Curie, Paris)

;;; Researcher at LIP6
;; (Computer science laboratory)



;;; # Let's get started ...

(ns latte-euroclojure-2016.core
  "This is a talk about LaTTe given @ Euroclojure 2016."

  ;; These belong to logic ;-)
  (:refer-clojure :exclude [and or not])

  ;; LaTTe core and main top-level forms
  (:require [latte.core :as latte
             :refer [definition defthm defaxiom defnotation
                     forall lambda ==>
                     assume have proof try-proof
                     term type-of check-type?]])

  ;; ... the "standard" library (propositions, quantifiers and equality) 
  (:require [latte.prop :as p :refer [<=> and or not]])
  (:require [latte.quant :as q :refer [exists]])
  (:require [latte.equal :as eq :refer [equal]]))



;;; # You know 'lambda', right?

;;; Well, in Clojure, 'lambda' is called 'fn'  (lazy guys ...)
;;; ... and it enables first-class higher-order anonymous functions ...

;;; ### A first example: the identity function

(fn [x] x)

;; ... that you can apply to some value:

((fn [x] x) 42)

;; what just happened is called a <<<beta-reduction|||t>>>
;; (a greek-ish translation for <<<function application|||t>>>)

;;; ### Another example: binary composition

((((fn [g] (fn [f] (fn [x] (f (g x)))))
   even?)
   (fn [y] (if y "even" "odd")))
 42)



;;; # Lambda the ultimate

;;; ## Church thesis
;;; All computable functions can be encoded in the pure lambda-calculus
;;(only with single argument <<<fn|||t>>>'s and function application)

;;; ### Example: the pairing function

(def pair (fn [x] (fn [y] (fn [z] ((z x) y)))))

;;; and accessors

(def fst (fn [p] (p (fn [x] (fn [y] x)))))
(def snd (fn [p] (p (fn [x] (fn [y] y)))))

;;; e.g.:

(fst ((pair "hello") 42))
(snd ((pair "hello") 42))



;;; # LaTTe (kernel) = Lambda with explicit types

;;; ###wait ...
;;;                _..._
;;;              .'     '.
;;;             /`\     /`\    |\
;;;            (__|     |__)|\  \\  /|
;;;            (     "     ) \\ || //
;;;             \         /   \\||//
;;;              \   _   /  |\|`  /
;;;               '.___.'   \____/
;;;                (___)    (___)
;;;              /`     `\  / /
;;;             |         \/ /
;;;             | |     |\  /
;;;             | |     | "`
;;;             | |     |
;;;             | |     |
;;;             |_|_____|
;;;            (___)_____)
;;;            /    \   |
;;;           /   |\|   |
;;;          //||\\  Y  |
;;;         || || \\ |  |
;;;         |/ \\ |\||  |
;;;             \||__|__|
;;;              (___|___)
;;;         jgs  /   A   \
;;;             /   / \   \
;;;            \___/   \___/




;;; # Types, really?

;;; Yes! in LaTTe the 'lambda' abstractions are
;;; explicitely typed...

;;; ### Example: the (type-generic) identity function

(term (lambda [A :type]
        (lambda [x A] x)))

;;; Let's check its type ...

(check-type?
 (lambda [A :type]
   (lambda [x A] x))

 ;; is of type ...

 :type)

;; This looks like an important property of <<<implication|||t>>>: reflexivity!



;;; # Typing the composition function

(check-type?
 (lambda [A B C :type]
   (lambda [g (==> A B)]
     (lambda [f (==> B C)]
       (lambda [x A]
         (f (g x))))))

  ;; ... of type

 :type)


;; Wow! another property of implication: transitivity!

;;; ==> we've just experienced <<<Proposition-as-Type|||t>>> (PaT)
;;;     part of the <<<Curry-Howard correspondance|||t>>>



;;; # The type-generic pairing function

;; reminder
(def pair (fn [x] (fn [y] (fn [z] ((z x) y)))))

;; type type-generic version
(check-type? [A :type] [B :type] ;; <-- this is called the 'context'
   (lambda [x A]
     (lambda [y B]
       (lambda [C :type]
         (lambda [z (==> A B C)]
           ((z x) y)))))
   ;; of type...
   :type)


;;; # The logical view: and-intro

;; In LaTTe the pairing function is:

latte.prop/and-intro

;; of type

;;; (==> A B
;;;      (and A B))

;; And in logic (natural deduction)
;; this is called the <<<introduction rule for conjunction|||t>>>:

;;;     A      B
;;;   ------------ (and-intro)
;;;     (and A B)



;;; # Elimination rules

;; reminders
(def fst (fn [p] (p (fn [x] (fn [y] x)))))
(def snd (fn [p] (p (fn [x] (fn [y] y)))))

(check-type? [A :type] [B :type] ;; <-- this is called 'the context'
   (lambda [p (and A B)]
     ((p A) (lambda [x A] (lambda [y B] x))))
   ;; of type
   :type)

;; --> this is <<<latte.prop/and-elim-left|||t>>>

(check-type? [A :type] [B :type] ;; <-- this is called 'the context'
   (lambda [p (and A B)]
     ((p B) (lambda [x A] (lambda [y B] y))))
   ;; of type
   :type)

;; --> this is <<<latte.prop/and-elim-right|||t>>>



;;; # Our first proof ...

;;; Let's try to prove something about
;;; the implication and conjunction

(check-type? [A :type] [B :type] [C :type]
  :type
             
  (==> (==> A B) (and C A)
       B))

;; This is the <<<Proof-as-Term|||t>>> part
;;     of the <<<Curry-Howard correspondance|||t>>> 



;;; # Entracte ...

;;; ## What we learned thus far ...

;;; that a lambda-calculus with types could be used to:

;;;   1) express logical propositions as types

;;;   2) prove the propositions using terms carying those types

;;; ## However ...

;;;   working directly with proofs-as-terms is cumbersome

;;;   and better <<<abstractions|||t>>> are required

;;; ---> so let's introduce the <<<LaTTe proof assistant|||t>>>



;;; # The LaTTe proof assistant

;;; ## Proof assistant
;;; a tool that allows to describe mathematical content on
;;; a computer, and assists in the principal activity of
;;; mathematicians: proving things!

;;; ## About LaTTe
;;; unlike most assistants, LaTTe is not a standalone application
;;; but a (Clojure) library (other examples are: HOL4 and HOL light).

;;; ## Main features
;;; - the kernel is a lambda-calculus with dependent types
;;;   (sometimes called λD of the calculus of constructions)
;;; - it proposes top-level (Clojure) forms for definitions, axioms, declaration
;;;   of theorems and encoding of proofs
;;; - it supports a DSL for declarative proof scripts <<<<-- hot!|||t>>>
;;; - it leverages the Clojure (JVM/Maven) ecosystem for <<<proving in the large|||t>>>



;;; # Our first theorem ...

(defthm first-theorem
  "This is an example theorem"
  [[A :type] [B :type] [C :type]]
  (==> (==> A B) (and C A)
       B))


(proof first-theorem
    :term
  (lambda [H1 (==> A B)]
    (lambda [H2 (and C A)]
      (H1 ((p/and-elim-right C A) H2)))))

