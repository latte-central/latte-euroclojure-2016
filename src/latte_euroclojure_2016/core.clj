
;;; # Live-coding Mathematics
;;; ## Your first Clojure proofs

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
;; (Université Pierre & Marie Curie, Paris - France)

;;; Researcher at LIP6
;; (Computer science laboratory)



;;; # To give credit where credit is due ...


;;; The theory underlying LaTTe (as well as its basic library)
;;; is heavily influenced by the following book:


;;; ## Type Theory and Formal Proof: an Introduction
;;; ### Rob Nederpelt and Herman Geuvers
;; Cambridge University Press, 2012

;;; It is a wonderful book for (the few ?) people interested
;;; in such topics

;; (but of course, you do *not* need to read the book to
;; use LaTTe, or hopefully to understand this talk!).




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



;;; # You know `lambda`, right?

;;; Well, in Clojure, `lambda` is called `fn`  (Oh laziness!)
;;; ... used to construct first-class anonymous functions ...

;;; ### A trivial example: the identity function

(fn [x] x)

;; ... that you can apply to some value:

((fn [x] x) 42)

;; what just happened is called a <<<beta-reduction|||t>>>
;; (a greek-ish translation for <<<function application|||t>>>)

;;; ### Another example: binary composition

((((fn [g] (fn [f] (fn [x] (f (g x)))))
   even?)                               ;; (==> int boolean)
   (fn [y] (if y "even" "odd")))        ;; (==> boolean String)
 42)



;;; # Lambda the ultimate

;;; ## Church thesis
;;; All computable functions can be encoded in the pure lambda-calculus
;;(only with single argument <<<fn|||t>>>'s, function application and variables)

;;; ### Example: the pairing function

(def pair (fn [x] (fn [y] (fn [z] ((z x) y)))))

;;; and accessors (or eliminators as you'll see)

(def fst (fn [p] (p (fn [x] (fn [y] x)))))
(def snd (fn [p] (p (fn [x] (fn [y] y)))))

;;; e.g.:

(fst ((pair "hello") 42))
(snd ((pair "hello") 42))



;;; # LaTTe (kernel) = Lambda with explicit types
;;; ## (a.k.a. a Type Theory)
;;;                _..._
;;;              .'     '.
;;;             /`\     /`\    |\         <<<but...|||(lambda (x) t)>>>
;;;            (__|     |__)|\  \\  /|
;;;            (     "     ) \\ || //
;;;             \         /   \\||//            <<<wait!?!|||(lambda (x) t)>>>
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

;;; Let's do some <<<type checking|||(lambda (x) t)>>>

(check-type?
 (lambda [A :type]
   (lambda [x A] x))

 ;; is of type ...

 :type)



;;; # Typing the composition function

(check-type?
 (lambda [A B C :type]
   (lambda [g (==> A B)]
     (lambda [f (==> B C)]
       (lambda [x A]
         (f (g x))))))

  ;; ... of type

 :type)



;;; # The logical view...

;;; Given arbitrary types A, B and C

;;; ### - the type of the identity function on A is:

;;; (==> A A)
;; "A implies A"  (reflexivity of implication)

;;; ### - the type of the composition function on A,B and C is:

;;; (==> (==> A B) (==> B C)
;;;      (==> A C))
;; if "A implies B" and "B implies C" then "A implies C"
;; (transitivity of implication)

;;; ==> we've just experienced <<<Proposition-as-Type|||t>>> (PaT)
;;;     part of the <<<Curry-Howard correspondance|||t>>>



;;; # The type-generic pairing function

;; reminder
(def pair (fn [x] (fn [y] (fn [z] ((z x) y)))))

;; the type-generic version
(check-type? [A :type] [B :type] ;; <-- this is called the 'context'
   (lambda [x A]
     (lambda [y B]
       (lambda [C :type]
         (lambda [z (==> A B C)]
           ((z x) y)))))
   ;; of type...
   :type)



;;; # Elimination rules

;; reminders
(def fst (fn [p] (p (fn [x] (fn [y] x)))))
(def snd (fn [p] (p (fn [x] (fn [y] y)))))

(check-type? [A :type] [B :type]
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



;;; # The missing piece

;;; Logical (and thus mathematical) reasoning heavily relies on
;;; a simple albeit powerful law: <<<modus ponens|||(lambda (x) t)>>> (a.k.a. deduction)

;;; ### if we know that "A implies B"
;;; ### and if it is the case that "A holds"
;;; ### ... then we can deduce that "B holds" also.

(check-type? [A :type] [B :type]
   :type
             
   (==> (==> A B) A
        B))

;;; {hide}
;; Modus ponens is beta-reduction (function application) it is that simple!
;;; {show}



;;; # Our first (low-level) proof ...

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

;;; ... that a lambda-calculus with types may be used to:

;;;   1) express logical propositions as types

;;;   2) formalise proofs of the propositions as terms carrying those types

;;; ## However ...

;;;   working directly with proofs-as-terms is cumbersome

;;;   and better <<<abstractions|||t>>> are required

;;; ---> so let's introduce the <<<LaTTe proof assistant|||t>>>



;;; # The LaTTe proof assistant

;;; ## Proof assistant
;;; a tool that allows to describe mathematical content on
;;; a computer, and assists in the mathematician's routine: proving things!

;;; ## About LaTTe
;;; unlike most assistants, LaTTe is not a standalone application
;;; but a Clojure library (available on Clojars!).
;;; ### ⟹ any Clojure Dev. Env. can be used to do maths!
;;; (e.g. I use both Cider and Gorilla Repl, sometimes together via nrepl...)

;;; ## Main features
;;;
- the kernel is a lambda-calculus with dependent types
;;;   (sometimes called λD or the calculus of constructions)
;;; - top-level Clojure forms are provided for definitions, axioms, declaration
;;;   of theorems and encoding of proofs (plus notations, specials, etc.)
;;; - it supports a DSL for declarative proof scripts <<<<-- hot!|||t>>>
;;; - it leverages the Clojure (JVM/Maven) ecosystem for <<<proving in the large|||t>>>



;;; # Our first theorems ...

(defthm and-elim-right
  "Right elimination for conjunction."
  [[A :type] [B :type]]
  (==> (and A B)
       B))

(defthm dummy-theorem
  "This is an example theorem"
  [[A :type] [B :type] [C :type]]
  (==> (==> A B) (and C A)
       B))

(proof dummy-theorem
    :term
  (lambda [H1 (==> A B)]
    (lambda [H2 (and C A)]
      (H1 ((and-elim-right C A) H2)))))



;;; # Let's do some real maths...

;;; Our objective is to encode in LaTTe the <<<Peano arithmetics|||t>>>
;;; for natural numbers, and demontrate an important inductive property
;;; about them...



;;; # The Peano arithmetics in the blink of an eye

"The first Peano primive: ℕ is a primitive set"

"The second Peano primitive: 0 is in ℕ"

"The third Peano primitive: the successor function of type ℕ ⟶ ℕ"

"The first Peano axiom: there is no successor in ℕ that equals 0"

"The second Peano axiom: the successor function is injective"

"The third Peano axiom: induction principle on ℕ"



;;; # A proof by induction

(defthm nat-strong
  "A natural integer is either zero or the successor of
another integer"
  []
  (forall [n nat]
    (or (equal nat n zero)
        (exists [m nat]
          (equal nat n (succ m))))))



;;; # Yes, we could!
;;; (I hope you enjoyed the ride...)

;;; ### Mathematics can be fun, (almost) as fun as live-coding in Clojure!
;; but ... wait.. this *is* live-coding in Clojure!

;;; Formalizing and proving things is a very addictive <<<puzzle game|||(lambda (x) t)>>>
;;; - with both a *single player mode* and *multiplayer cooperation* available!
;; (MMO being considered)
;;; - simple puzzles for starters: propositional logic, basic quantifiers, etc.
;;; - and more challenging things: inductive types, numbers, etc.
;;    (way better than Sudoku and even kakuro)
;;; - also very difficult things: current mathematics
;;; - and what about <<<P =/≠ NP|||(lambda (x) t)>>>?
;;    (if you enjoy real challenges!)

;;; ### Let's play together at: https://github.com/fredokun/LaTTe
;;; you're just a `lein new my-cool-maths` away...
;; (no? really? how unfortunate :~( )


;;; # Thank you!
