
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

;;;  LIP6 (Computer science laboratory)
;;;  UPMC (University Pierre & Marie Curie, Paris - France)



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
 (λ [A :type] (λ [x A] x))
  ;; ^^^ (fn [x] x) in LaTTe ^^^
)





;;; # Let's do some type-checking ...

(type-check?

 ;; the lambda-term:

 (λ [A :type]
   (λ [x A] x))

 ;; is of type ...

 (∀ [A :type]
  (==> A A)))



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

 (∀ [A B C :type]
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
 (λ [f (==> A B)]
    (λ [x A]
       (f x)))
 
 ;; of type
 
 (==> (==> A B) A
      B))



;;; # Universal quantifier

;;; In type theory, the modus ponens naturally generalizes
;;; to instantiation of universal quantifiers.

(type-check?
 [Thing :type] [man (==> Thing :type)] [mortal (==> Thing :type)]
 [socrates Thing]

 (λ [H1 (∀ [t Thing]
         (==> (man t) (mortal t)))]
    (λ [H2 (man socrates)]
       ((H1 socrates) ;; (==> (man socrates) (mortal socrates))
        H2)))
 
 ;; ^^^ Was Aristotle right? ^^^
 
 (==> (∀ [t Thing]
        (==> (man t) (mortal t)))
      (man socrates)
      ;; thus
      (mortal socrates)))



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

;;; 2) a peek at <<<equality|||(lambda (x) t)>>> (according to Mr.Leibniz)

;;; 3) take a dip in "real" <<<mathematics|||(lambda (x) t)>>>




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
  (∀ [C :type]
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
                  f (==> A (==> B C))]
            (have <a> (==> B C) :by (f x))
            (have <b> C :by (<a> y))
            (have <c> (forall [C :type]
                         (==> (==> A B C)
                              C))
                  :discharge [C f <b>])) ;; (λ [C :type] (λ [f (==> A B C)] <b>))
         (qed <c>)))



;;; # Conjunction in Type Theory (2/2)

;;; Finally the elimination rules

;;;     (and A B)                         (and A B)
;;;  ============== (and-elim-left)    ============== (and-elim-right)
;;;        A                                 B

(defthm and-elim-left- ""
  [[A :type] [B :type]]
  (==> (and- A B)
       A))

(proof and-elim-left-
    :script
  (assume [H (and- A B)]
    (have <a> (==> (==> A B A) A) :by (H A))
    (assume [x A
             y B]
      (have <b> A :by x)
      (have <c> (==> A B A) :discharge [x y <b>])) ;; (λ [x A] (λ [y B] x))
    (have <d> A :by (<a> <c>))
    (qed <d>)))

;; exercice : and-elim-right-  (solution? p/and-elim-right)



;;; # Conjunction introduction looks familiar!

;;; Let's slowly remove types in <<<and-intro-|||(lambda (x)t)>>>

(type-check?
 [A :type] [B :type]

 (λ [x A] (λ [y B] (λ [C ✳] (λ [f (Π [⇧ A] (Π [⇧' B] C))] [[f x] y]))))
 ;; ^^^ and-intro- as a term ^^^

 (==> A B
      (and- A B)))

;; In Clojure :
(fn [x] (fn [y] (fn [f] ((f x) y))))




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
(def intro (fn [x] (fn [y] (fn [f] ((f x) y)))))
(def left (fn [p] (p (fn [x] (fn [y] x)))))
(def right (fn [p] (p (fn [x] (fn [y] y)))))

;; Have these functions any computational meaning?

(def p ((intro "hello") 42))
(left p)
(right p)



;;; # 2) A peek at equality

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
;; except for referential equality)

(= [1 2 3 4] (range 1 5))

;; breaking equal
(seq? [1 2 3 4])
(seq? (range 1 5))

;; breaking eq/cong
(get [1 2 3 4] 2)
(get (range 1 5) 2)



;;; # 3) Some "real" mathematics

(definition injective
  "A function f is injective iff for ∀x,y, f(x)=f(y) ⟹ x=y."
  [[T :type] [U :type] [f (==> T U)]]
  (∀ [x y T]
   (==> (equal U (f x) (f y))
        (equal T x y))))

(defthm compose-injective
  "if f and g are injective functions, then f°g is injective too"
  [[T :type] [U :type] [V :type] [f (==> U V)] [g (==> T U)]]
  (==> (injective U V f)
       (injective T U g)
       (injective T V (λ [x T] (f (g x))))))

(proof compose-injective
    :script
    "Our hypothesis is that f and g are injective."
    (assume [Hf (injective U V f)
             Hg (injective T U g)]
      "We then have to prove that the composition is injective."
      "For this we consider two arbitrary elements x and y
 such that f(g(x)) = f(g(y))"
      (assume [x T
               y T
               Hxy (equal V (f (g x)) (f (g y)))]
        "Since f is injective we have: g(x) = g(y)."
        (have <a> (equal U (g x) (g y)) :by (Hf (g x) (g y) Hxy))
        "And since g is also injective we obtain: x = y."
        (have <b> (equal T x y) :by (Hg x y <a>))
        "Since x and y are arbitrary, f°g is thus injective."
        (have <c> (∀ [x y T]
                   (==> (equal V (f (g x)) (f (g y)))
                        (equal T x y)))
              :discharge [x y Hxy <b>]))
    "Which is enough to conclude the proof."
    (qed <c>)))


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

