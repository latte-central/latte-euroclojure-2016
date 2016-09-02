
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

(ns latte-euroclojure-2016.full
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
;;; ... we use it to construct first-class anonymous functions ...

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

;; e.g.:  (((lambda [A :type] (lambda [x A] x)) 42) int)
;;        --> ((lambda [x int] x) 42)
;;        --> 42



;;; # Let's type-check ...

;;; ##What is the type of the (type-generic) identity?

(check-type?
 (lambda [A :type]
   (lambda [x A] x))

 ;; is of type ...

 (forall [A :type]
   (==> A A)))

;; or (forall [A :type]
;;      (forall [x A] A))



;;; # The type-generic composition function

(check-type?
 (lambda [A B C :type]
   (lambda [g (==> A B)]
     (lambda [f (==> B C)]
       (lambda [x A]
         (f (g x))))))

  ;; ... of type

 (forall [A B C :type]
   (==> (==> A B) (==> B C)
        (==> A C))))



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

;; a type-generic version
(check-type? [A :type] [B :type] ;; <-- this is called the 'context'
   (lambda [x A]
     (lambda [y B]
       (lambda [C :type]
         (lambda [z (==> A B C)]
           ((z x) y)))))
   ;; of type...

   (==> A B
        (and A B)))

;; or  (==> A B
;;       (forall [C :type]
;;          (==> (==> A B C)
;;               C)))



;;; # Accessors = elimination rules

;; reminders
(def fst (fn [p] (p (fn [x] (fn [y] x)))))
(def snd (fn [p] (p (fn [x] (fn [y] y)))))

(check-type? [A :type] [B :type]
   (lambda [p (and A B)]
     ((p A) (lambda [x A] (lambda [y B] x))))
   ;; of type
   (==> (and A B)
        A))

;; --> this is <<<latte.prop/and-elim-left|||t>>>

(check-type? [A :type] [B :type] ;; <-- this is called 'the context'
   (lambda [p (and A B)]
     ((p B) (lambda [x A] (lambda [y B] y))))
   ;; of type
   (==> (and A B)
        B))

;; --> this is <<<latte.prop/and-elim-right|||t>>>



;;; # Deduction ...

;;; Logical (and thus mathematical) reasoning heavily relies on
;;; a simple albeit powerful law: <<<modus ponens|||(lambda (x) t)>>>
;; (a.k.a. detachment, cut, resolution, etc.)

;;; ### if we know that "A implies B"
;;; ### and if it is the case that "A holds"
;;; ### ... then we can deduce that "B holds" also.

(check-type? [A :type] [B :type]

   (lambda [f (==> A B)]
     (lambda [x A]
       (f x)))

   ;; of type
   
   (==> (==> A B) A
        B))


;; Modus ponens is beta-reduction (function application) it is that simple!




;;; # Our first (low-level) proof ...

;;; Let's try to prove something about
;;; the implication and conjunction

(check-type? [A :type] [B :type] [C :type]
             
  (lambda [H1 (==> A B)]
    (lambda [H2 (and C A)]
      (H1 ((H2 A) (lambda [x C] (lambda [y A] y))))))

  ;; ^^^ the proof term ^^^
  
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

;;; - the kernel is a lambda-calculus with dependent types
;;   (sometimes called λD or the calculus of constructions)
;;; - top-level Clojure forms are provided for definitions, axioms, declaration
;;;   of theorems and encoding of proofs
;; (plus notations, specials, etc.)
;;; - it supports a DSL for declarative proof scripts <<<<-- hot!|||t>>>
;;; - it leverages the Clojure (JVM/Maven) ecosystem for <<<proving in the large|||t>>>



;;; # Our first LaTTe theorems ...

(defthm and-elim-right
  "Right elimination for conjunction."
  [[A :type] [B :type]]
  (==> (and A B)
       B))

;; Warning: proof required !
(proof and-elim-right
    :term
  (lambda [H (and A B)]
    ((H B) (lambda [x A] (lambda [y B] y)))))

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

;;; ### Our objective (in the few minutes to come):

;;; 1) live-code in LaTTe the <<<Peano arithmetics|||t>>>
;;; for natural numbers, and

;;; 2) demontrate an important inductive property
;;; about them...



;;; # The Peano arithmetics
;;; ### in (a bunch of) blinks of an eye

(defaxiom nat
  "The first Peano primive: ℕ is a primitive set"
  []
  :type)

(defaxiom zero
  "The second Peano primitive: 0 is in ℕ"
  []
  nat)

(defaxiom succ
"The third Peano primitive: the successor function of type ℕ ⟶ ℕ"
  []
  (==> nat nat))

(defaxiom nat-zero
  "The first Peano axiom: there is no successor in ℕ that equals 0"
  []
  (forall [n nat]
    (not (equal nat (succ n) zero))))

(defaxiom nat-succ-inj
  "The second Peano axiom: the successor function is injective"
  []
  (forall [n m nat]
    (==> (equal nat (succ n) (succ m))
         (equal nat n m))))

(defaxiom nat-induct
  "The third Peano axiom: induction principle on ℕ"
  [[P (==> nat :type)]]
  (==> (and (P zero)
            (forall [k nat]
              (==> (P k) (P (succ k)))))
       (forall [n nat] (P n))))



               


;;; # A proof by induction

(defthm nat-strong
  "A natural integer is either zero or the successor of
another integer"
  []
  (forall [n nat]
    (or (equal nat n zero)
        (exists [m nat]
          (equal nat n (succ m))))))

(proof nat-strong
    :script
  "We first define the predicate P(n) corresponding to nat-strong."
  (have P _ :by (lambda [n nat]
                  (or (equal nat n zero)
                      (exists [m nat]
                        (equal nat n (succ m))))))
  "We now prove P(n) by induction on n."
  "1) base case P(0)"
  "0 = 0  by reflexivity"
  (have base1 (equal nat zero zero)
        :by (eq/eq-refl nat zero))
  "hence P(0) holds."
  (have base (P zero)
        :by ((p/or-intro-left
              (equal nat zero zero)
              (exists [m nat]
                (equal nat zero (succ m)))) base1))
  "2) inductive case"
  "We suppose that P(k) holds for an arbitrary k."
  (assume [k nat
           Hind (P k)]
    "We then have to show that P(k+1) holds."
    "Let the predicate Q(m) such that k+1=m+1"
    (have Q _ :by (lambda [m nat]
                    (equal nat (succ k) (succ m))))
    "Since  k+1 = k+1 by reflexivity we know that Q(k) is true."
    (have induct1 (Q k)
          :by (eq/eq-refl nat (succ k)))
    "hence there exists an m such that k+1=m+1 (namely k)"
    (have induct2 (exists [m nat]
                    (equal nat (succ k) (succ m)))
          :by ((q/ex-intro nat Q k) induct1))
    "from this we get that P(k+1) is true."
    (have induct3 (P (succ k))
          :by ((p/or-intro-right (equal nat (succ k) zero)
                                 (exists [m nat]
                                   (equal nat (succ k) (succ m)))) induct2))
    "hence forall k, P(k) ==> P(k+1) as planned."
    (have induct (forall [k nat]
                   (==> (P k) (P (succ k))))
          :discharge [k Hind induct3]))
  "we can conclude by applying the induction axiom."
  (qed ((nat-induct P) ((p/and-intro (P zero)
                                     (forall [k nat] (==> (P k) (P (succ k)))))
                        base
                        induct))))
    


;;; # Yes, we could!
;;; (I hope you enjoyed the ride...)

;;; ### Mathematics can be fun, (almost) as fun as live-coding in Clojure!
;; but ... wait! this *is* live-coding in Clojure!

;;; Formalizing and proving things can be a very addictive <<<puzzle game|||(lambda (x) t)>>>
;;; - with both a <<<single player mode|||(lambda (x) t)>>> and <<<multiplayer cooperation|||(lambda (x) t)>>> available!
;; (MMO being considered)

;;; - An un limited number of puzzles awaits you:

;;;    * starters: propositional logic, basic quantifiers, etc.

;;;    * serious challenges: numbers, inductive types, recursive functions, etc.
;;    (way better than Sudoku and even kakuro)

;;;    * professional-grade puzzles: modern mathematics



;;; # ... and what about a real challenge?

(defthm life-universe-rest
  "" [[Algos :type] [P Algos] [NP Algos]]
  (not (equal Algos P NP))) ;; or is-it?

(proof life-universe-rest
    :script
  "TODO")

;;; ### Let's play together at: https://github.com/fredokun/LaTTe
;;; you're just a `lein new my-cool-maths` away...
;; (no? really? how unfortunate :~( )


;;; # Thank you!
