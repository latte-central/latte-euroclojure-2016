
;;; # Live-coding Mathematics
;;; ## Your first Clojure proofs

;;  using the LaTTe proof assistant: <<<https://github.com/fredokun/LaTTe|||(lambda (x) (browse-url "https://github.com/fredokun/LaTTe"))>>>

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



;;;               __...--~~~~~-._   _.-~~~~~--...__
;;;             //               `V'               \\ 
;;;            //                 |                 \\ 
;;;           //__...--~~~~~~-._  |  _.-~~~~~~--...__\\ 
;;;          //__.....----~~~~._\ | /_.~~~~----.....__\\
;;;         ====================\\|//====================
;;;                         dwb `---`  



;;; # Let's get started ...

(ns latte-euroclojure-2016.core
  "This is a talk about LaTTe given @ Euroclojure 2016."

  ;; These belong to logic and mathematics ;-)
  (:refer-clojure :exclude [and or not set complement])

  ;; LaTTe core and main top-level forms
  (:require [latte.core :as latte
             :refer [definition defthm defaxiom defnotation
                     forall lambda ==>
                     assume have proof try-proof
                     term type-of type-check?]]

  ;; ... the "standard" library (propositions, quantifiers and equality) 
            [latte.prop :as p :refer [<=> and or not]]
            [latte.quant :as q :refer [exists]]
            [latte.equal :as eq :refer [equal]]))



;;; # You know `lambda`, right?

;;; Well, in Clojure, `lambda` is called `fn`  (Oh laziness!)
;;; ... we use it to construct first-class anonymous functions ...

;;; ### A trivial example: the identity function

((fn [x] x) 42)

;;; ### Another example: binary composition: g°f

((((fn [f] (fn [g] (fn [x] (g (f x)))))
   even?)                               ;; (==> int boolean)
   (fn [y] (if y "even" "odd")))        ;; (==> boolean String)
 42)



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

;;; ### Example: the (type-generic) identity function

(term
 ✳
 ;; ^^^ (fn [x] x) in LaTTe ^^^
 )

;; e.g.:  ((fn [x] x) 42)
;;        
;;        --> 42



;;; # Let's do some type-checking ...

(type-check?

 ;; the lambda-term:

 (λ [A :type]
   (λ [x A] x))

 ;; is of type ...

 ✳)



;;; # The type-generic composition function
;; ((fn [f] (fn [g] (fn [x] (g (f x)))))) in LaTTe

(type-check?

  ;; the lambda-term:

 (λ [A B C :type]
   (λ [f (==> A B)]
     (λ [g (==> B C)]
       (λ [x A]
         (g (f x))))))

 ;; is of type ...

 (forall [A B C :type]
  (==> (==> A B)
       (==> B C)
       (==> A C))))



;;; # Deduction ...

;;; Logical (and thus mathematical) reasoning heavily relies on
;;; a simple albeit powerful law: <<<modus ponens|||(lambda (x) t)>>>
;; (a.k.a. detachment, cut, resolution, etc.)

;;; ### if we know that "A implies B"
;;; ### and if it is the case that "A holds"
;;; ### ... then we can deduce that "B holds" also.

(type-check?
 [A :type] [B :type]

 ;; find a term ...
 ✳
 
 ;; of type
 
 (==> (==> A B) A
      B))



;;; # Universal quantifier

;;; In type theory, the modus ponens naturally generalizes
;;; to instantiation of universal quantifiers.

(type-check?
 [Thing :type] [man (==> Thing :type)] [mortal (==> Thing :type)]
 [socrate Thing]

 ✳
 
 ;; ^^^ Was Aristotle right? ^^^
 
 (==> (forall [t Thing]
        (==> (man t) (mortal t)))
      (man socrate)
      ;; thus
      (mortal socrate)))




;;; # Entracte ...

;;; ## What we learned thus far ...

;;; ... that thanks to the Curry-Howard correspondence
;;; a lambda-calculus with types may be used to:

;;;   1) express logical <<<propositions as types|||(lambda(x)t)>>>

;;;   2) formalize <<<proofs as terms|||(lambda(x)t)>>> carrying those types

;;; ## However ...

;;;   working directly with proofs-as-terms is cumbersome

;;;   and better <<<abstractions|||t>>> are required

;;; ---> so let's introduce the <<<LaTTe proof assistant|||t>>>



;;; # The LaTTe proof assistant

;;; ## Proof assistant
;;; a tool that allows to formulate mathematical content on
;;; a computer, and assists in the mathematician's routine: proving things!


;;; ## About LaTTe
;;; LaTTe is a proof assistant implemented as a Clojure library
;;; with top-level forms for axioms, definitions, theorems and proofs.
;; available on Clojars: <<<[latte "0.3.5-SNAPSHOT"]|||(lambda (x) t)>>>

;;; ## Notable features

;;; - any Clojure Development environment can be used to do maths!
;; (e.g. I use both Cider and Gorilla Repl, sometimes together via nrepl...)

;;; - it leverages the Clojure (JVM/Maven) ecosystem for <<<proving in the large|||t>>>

;;; - it supports a DSL for declarative proof scripts <<<<-- hot!|||t>>>



;;; # Let's do the maths...

;;; ### Our objectives (in the few minutes to come):

;;; 1) a glimpse of <<<natural deduction|||(lambda (x) t)>>> (logic)

;;; 2) a bit of (typed) <<<set theory|||(lambda (x) t)>>>

;;; 3) a peek at <<<equality|||(lambda (x) t)>>> (according to Mr.Leibniz)

;;; 4) a squint into <<<Peano arithmetics|||(lambda (x) t)>>>
;; (if time permits, but time won't !)



;;; # 1) a glimpse of Natural Deduction

;;; A simple characterization of (most) logical constructions
;;; based on <<<introduction rules|||(lambda (x)t)>>>
;; (how to construct?)
;;; and <<<elimination rules|||(lambda (x)t)>>>
;; (how to destruct?)

;;; ## Example: conjunction

;;; ### Introduction rule

;;;      A      B
;;;   ============== (and-intro)
;;;      (and A B)

;;; ### Elimination rules

;;;     (and A B)                         (and A B)
;;;  ============== (and-elim-left)    ============== (and-elim-right)
;;;        A                                 B



;;; # Conjunction in Type Theory (1/2)

;;; First a somewhat cryptic definition:

(definition and-  ;; nameclash!
  "Conjunction in Type Theory"
  [[A :type] [B :type]]
  (forall [C :type]
    (==> (==> A B C)
         C)))

;;; Then the introduction rule: 

;;;      A      B
;;;   ============== (and-intro)
;;;      (and A B)

(defthm and-intro- ""
  [[A :type] [B :type]]
  (==> A B
       (and- A B)))

(proof and-intro-
       :script
       (assume [x A
                y B]
         (assume [C :type
                  f (==> A B C)]
            (have <a> (==> B C) :by (f x))
            (have <b> C :by (<a> y))
            (have <c> (forall [C :type]
                         (==> (==> A B C)
                              C))
                  :discharge [C f <b>]))
         (qed <c>)))



;;; # Conjunction in Type Theory (2/2)

;;; Finally the elimination rules

;;;     (and A B)                         (and A B)
;;;  ============== (and-elim-left)    ============== (and-elim-right)
;;;        A                                 B

(defthm and-elim-left- ""
  [[A :type] [B :type]]
   ✳)

(proof and-elim-left-
    :script
  "todo")

;; exercice : and-elim-right-  (solution? p/and-elim-right)



;;; # Conjunction introduction looks familiar!

;;; Let's slowly remove types in <<<and-intro-|||(lambda (x)t)>>>

(type-check?
 [A :type] [B :type]

 ;; ^^^ and-intro- as a term ^^^

 (==> A B (and- A B)))

;; In Clojure :




;;; # Conjunction elimination looks familiar too!

;;; Let's slowly remove types in <<<and-elim-left-|||(lambda (x)t)>>>

(type-check?
 [A :type] [B :type]

 (λ [p (and- A B)] [[p A] (λ [x A] (λ [y B] x))])

 ;; ^^^ and-elim-left- as a term ^^^

 (==> (and- A B) A))

;; In Clojure
(fn [p] (p (fn [x] (fn [y] x))))




;;; # Conjunction as computation (in Clojure)

;; We have:
(def mk-and (fn [x] (fn [y] (fn [f] ((f x) y)))))
(def left (fn [p] (p (fn [x] (fn [y] x)))))
(def right (fn [p] (p (fn [x] (fn [y] y)))))

;; Have these functions any computational meaning?

(def p ((mk-and "hello") 42))
(left p)
(right p)



;;; # 2) a bit of (typed) Set Theory

;;; Why the set <<<{p:program | p halts}|||(lambda(x)t)>>> cannot be a type in LaTTe?
;;; ==> because unlike type inhabitation, set membership is not decidable

;;; So how to represent a (typed) set in LaTTe?
;;; A very effective approach is to use consider <<<sets as predicates|||(lambda(x)t)>>>

(definition set
  "The type of a set in type theory"
  [[T :type]]
  (==> T :type)) ;; a predicate over T

;;; ### Set membership

(definition elem   ;; x∈s = (elem T x s)
  "Set membership"
  [[T :type] [x T] [s (set T)]]
  (s x))

;;; ### Example 1: intersection

(definition intersection
  "s1 ∩ s2"
  [[T :type] [s1 (set T)] [s2 (set T)]]
  (λ [x T]
    (and (elem T x s1)
         (elem T x s2))))

;;; ### Example 2: the complement of a set of type T

(definition complement ""
  [[T :type] [s (set T)]]
  (λ [x T]
    (not (elem T x s))))

;;; ### Complement in classical set theory:
;;; ∁(A) = { x ∈ U | x ∉ A }.
;; but what is the "universe" U?



;;; # Sets in Clojure

;;; ### Interestingly ...

;;; In Clojure a (finite) set is also a predicate!

(#{1 2 4} 2)

(#{1 2 4} 5)

;; in Latte:
;;; (λ [x nat] (or (equal nat x 1) (equal nat x 2) (equal nat x 4)))



;;; # 3) A peek at equality

;;; Equality is a non-trivial programming aspect

;;; There are basically two approaches:

;;; 1) polymorphic equality (e.g. Clojure, Common Lisp, Ocaml)

;;; 2) user-definable equality (e.g. Java, Haskell, Python)

;;; ... with strengthes and drawbacks, but
;;; in both cases one can easily shoot oneself in the foot...
;; (think about testing...)

;;; ### Question
;;; What about a  logical characterization? 



;;; # Leibniz's indiscernibility of identicals

(definition equal- ;; nameclash
  "Mr. Leibniz says..."
  [[T :type] [x T] [y T]]
  (forall [P (==> T :type)]
    (<=> (P x) (P y))))

;; As a consequence:

(defthm eq-cong- ""
  [[T :type] [U :type] [f (==> T U)]
   [x T] [y T]]
  (==> (equal- T x y)
       (equal- U (f x) (f y))))

;; (proof is non-trivial, cf. <<<latte.equal/eq-cong|||(lambda(x)t)>>>)

;;; ### Clojure counter-example
;; (but there's one for any programming language
;; except for pointer equality)

(= [1 2 3 4] (range 1 5))

;; breaking equal
(seq? [1 2 3 4])
(seq? (range 1 5))

;; breaking eq/cong
(get [1 2 3 4] 2)
(get (range 1 5) 2)



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

(defaxiom nat-succ-not-surj
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
  (==> (P zero)
       (forall [k nat]
         (==> (P k) (P (succ k))))
       (forall [n nat] (P n))))



;;; # A proof by induction

(defthm nat-case
  "Proof by case analysis."
  [[P (==> nat :type)]]
  (==> (P zero)
       (forall [k nat] (P (succ k)))
       (forall [n nat] (P n))))

(proof nat-case
  :script
  "First we state our assumptions."     
  (assume [Hz (P zero)
           HS (forall [k nat] (P (succ k)))]
          "Now we proceed by induction on n."
          "base case (n=0): trivial since (P zero) by Hz"
          "inductive case. Suppose (P k) for some natural number k"
    (assume [k nat
             Hind (P k)]
            "Let's prove that (P (succ k))"
            (have a (P (succ k)) :by (HS k))
            "Hence for any k (==> (P k) (P succ k))"
      (have b (forall [k nat]
                (==> (P k) (P (succ k))))
            :discharge [k Hind a]))
    "Thus (P n) is true for any n thanks to nat-induct."
    (have c (forall [n nat] (P n))
          :by ((nat-induct P) Hz b))
    (qed c)))

(definition nat-split
  "The split of natural numbers."
  [[n nat]]
  (==> (not (equal nat n zero))
       (exists [m nat]
         (equal nat n (succ m)))))
  
(defthm nat-strong
  "A natural integer that is not zero is the successor of
another integer"
  []
  (forall [n nat]
    (nat-split n)))

(proof nat-strong
    :script
  "We do the proof by case analysis on n."
  "1) case n=0 we show a contradiction"
  "The hypothesis is that zero<>zero"
  (assume [Hz (not (equal nat zero zero))]
    "but of course zero=zero by reflexivity"
    (have <a1> (equal nat zero zero) :by (eq/eq-refl nat zero))
    "hence there is a contradiction"
    (have <a2> p/absurd :by ((p/absurd-intro (equal nat zero zero))
                             <a1> Hz))
    "and from a contradiction we can get anything..."
    (have <a3> (exists [m nat]
                 (equal nat zero (succ m)))
          :by (<a2> (exists [m nat]
                      (equal nat zero (succ m)))))
    (have <a> (nat-split zero) :discharge [Hz <a3>]))
  "2) case n=k+1 for an arbitrary k"
  "The assumption is that k+1<>0 is distinct from zero (which is vacuously true,
but this is not in fact important)" 
    (assume [k nat
             Hk (not (equal nat (succ k) zero))]
      "Let the predicate Q(m) such that k+1=m+1"
      (have Q _ :by (lambda [m nat] (equal nat (succ k) (succ m))))
      "Since  k+1 = k+1 by reflexivity we know that Q(k) is true."
      (have <b1> (Q k)
            :by (eq/eq-refl nat (succ k)))
      "hence there exists an m such that k+1=m+1 (namely k)"
      (have <b2> (exists [m nat]
                   (equal nat (succ k) (succ m)))
            :by ((q/ex-intro nat Q k) <b1>))
      "hence we can deduce the case for k+1 from the case of k."
      (have <b> (forall [k nat]
                  (nat-split (succ k)))
            :discharge [k Hk <b2>]))
    "we can conclude by applying the case analysis theorem."
    (have <concl> _
          :by ((nat-case nat-split) <a> <b>))
    (qed <concl>))
    


;;; # Aftermath ...

;;; ### Mathematics can be fun, (almost) as fun as live-coding in Clojure!
;; but ... wait! this *is* live-coding in Clojure!

;;; Formalizing and proving things can be a very addictive <<<puzzle game|||(lambda (x) t)>>> with:

;;; - An almighty adversary: <<<mathematics|||(lambda(x)t)>>>

;;; - An <<<unlimited|||(lambda(x)t)>>> number of puzzles:

;;;    * starters: propositional logic, basic quantifiers, etc.

;;;    * serious challenges: numbers, inductive types, recursive functions, etc.
;;    (way more captivating than Sudoku and even kakuro)

;;;    * professional-grade puzzles: modern mathematics
;; (new puzzles invented everyday...)



;;; # ... and what about a real challenge?

(defthm one-million-dollar-baby
  "" [[Problem :type] [P (set Problem)] [NP (set Problem)]]
  (not (equal Problem P NP))) ;; or is-it?

(proof one-million-dollar-baby
    :script
  "TODO")

;;;
;;; ### Let's play together at: https://github.com/fredokun/LaTTe
;;; you're just a `lein new my-cool-maths-in-clojure` away...
;; (no? really? how unfortunate :~( )


;;; # Thank you!

