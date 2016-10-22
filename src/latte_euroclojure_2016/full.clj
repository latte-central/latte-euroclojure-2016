
;;; # Live-coding Mathematics
;;; ## Your first Clojure proofs

;;  using the LaTTe proof assistant: <<<https://github.com/fredokun/LaTTe|||(lambda (x) (browse-url "https://github.com/fredokun/LaTTe"))>>>

         ;;;                  ((((
         ;;;                 ((((
         ;;;                  ))))             or
         ;;;               _ .---.               <<<Curry|||lambda(x)t>>>-     
         ;;;              ( |`---'|                 <<<Howard|||lambda(x)t>>>
         ;;;               \|     |                       without the fuss
         ;;;               : .___, :
         ;;;                `-----'  -Karl

;;; ### Frédéric Peschanski @ Euroclojure 2016

;;;  LIP6 (Computer science laboratory)
;;;  UPMC (University Pierre & Marie Curie, Paris - France)



;;; # Acknowledgment ...
;; \ik-ˈnä-lij-mənt, ak-\

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

  ;; These belong to logic ;-)
  (:refer-clojure :exclude [and or not])

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



;;; # LaTTe (kernel) = a Type Theory
;;; ## = Lambda with explicit (dependent) types
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
;; in Clojure: ((fn [f] (fn [g] (fn [x] (g (f x))))))

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
       ((H1 socrates)  ;; (==> (man socrates) (mortal socrates))
        H2)))
 
 ;; ^^^ Was Aristotle right? ^^^
 
 (==> (∀ [t Thing]
        (==> (man t) (mortal t)))
      (man socrates)
      ;; thus
      (mortal socrates)))



;;; # The rules of the games ...
;; a.k.a. The LaTTe (kernel) calculus

;;; ### Syntax
;;; - <<<the type of types|||(lambda (x)t)>>>: :type (or ✳)
;;; - <<<the type of :type|||(lambda (x)t)>>>: :kind (or □)
;;; - <<<variables|||(lambda (x)t)>>>:  x, y, etc..
;;; - <<<abstractions|||(lambda (x)t)>>>: (λ [x <type>] <body>)
;;; - <<<products|||(lambda (x)t)>>>: (∀ [x <type>] <type>)
;;; - <<<applications|||(lambda (x)t)>>>: (<fun> <arg>)

;;; ### Alpha-conversion and equivalence
;;; t1 = t2  if they are the same up-to renaming of bound variables
;; e.g.:  (λ [x A] x) = (λ [y A] y)

;;; ### Beta-reduction (≅semantics)
;;; ((λ [x <type>] <body>) <arg>) ⟶ <body>{<arg>/x}
;; e.g.: ((λ [x A] (y x)) (a b)) ⟶ (y (a b))
;;       ((∀ [A :type] (f A))) int) ⟶ (f int)

;;; + <<<normalization|||(lambda (x)t)>>>: "all you can eat" beta-reduction



;;; # Entracte ...

;;; ## What we learned thus far ...

;;; ... that thanks to the Curry-Howard correspondence
;;; a lambda-calculus with dependent types may be used to:

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
;; available on Clojars: <<<[latte "0.3.7-SNAPSHOT"]|||(lambda (x) t)>>>

;;; ## Notable features

;;; - any Clojure Development environment can be used to do maths!
;; (e.g. I use both Cider and Gorilla Repl, sometimes together via nrepl...)

;;; - it leverages the Clojure (Clojars) ecosystem for <<<proving in the large|||t>>>

;;; - it supports a DSL for declarative proof scripts <<<<-- hot!|||t>>>



;;; # Let's do the maths...

;;; ### Our objectives (in the few minutes to come):

;;; 1) a bit of logic: <<<natural deduction|||(lambda (x) t)>>> 

;;; 2) a taste of "real" mathematics: <<<injectivity|||(lambda (x) t)>>>



;;; # 1) a bit of logic: Natural Deduction

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
    :term
  (λ [x A]
    (λ [y B]
       (λ [C :type]
          (λ [f (==> A B C)]
             ((f x) y))))))



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
  "Our hypothesis"     
  (assume [p (and- A B)]
    "The starting point: use the definition of conjunction."
    (have <a> (==> (==> A B A) A) :by (p A))
    "We need to prove that if A is true and B is true then A is true"
    (assume [x A
             y B]
      (have <b> A :by x)
      (have <c> (==> A B A) :discharge [x y <b>]))
    "Now we can use <a> as a function"
    (have <d> A :by (<a> <c>))
    (qed <d>)))

;; exercice : and-elim-right-  (solution? p/and-elim-right)



;;; # Conjunction as computation (in Clojure)

;; We have:
;; intro in Latte: (λ [x A] (λ [y B] (λ [C :type] (λ [f (==> A B C)] ((f x) y))))
(def intro (fn [x] (fn [y] (fn [f] ((f x) y)))))

;; elim-left: (λ [p (and- A B)] ((p A) (λ [x A] (λ [y B] x))))
(def left (fn [p] (p (fn [x] (fn [y] x)))))

;; elim-left: (λ [p (and- A B)] ((p B) (λ [x A] (λ [y B] y))))
(def right (fn [p] (p (fn [x] (fn [y] y)))))

;; Have these functions any computational meaning?

(def p ((intro "hello") 42))
(left p)
(right p)



;;; # 2) A taste of "real" mathematics
;;; ## Injectivity

(definition injective
  "A function f is injective iff for ∀x,y, f(x)=f(y) ⟹ x=y."
  [[T :type] [U :type] [f (==> T U)]]
  (∀ [x y T] (==> (equal U (f x) (f y))
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
      (have <a> (equal U (g x) (g y))
            :by (Hf (g x) (g y) Hxy))
      
      "And since g is also injective we obtain: x = y."
      (have <b> (equal T x y)
            :by (Hg x y <a>))

      "Since x and y are arbitrary, f°g is thus injective."
      (have <c> (injective T V (λ [x T] (f (g x))))
            :discharge [x y Hxy <b>]))

    "Which is enough to conclude the proof."
    (qed <c>)))



;;; # Aftermath ...

;;; ### Mathematics can be fun, (almost) as fun as live-coding in Clojure!
;; but ... wait! this *is* live-coding in Clojure!

;;; Formalizing and proving things can be a very addictive <<<puzzle game|||(lambda (x) t)>>> with:

;;; - Relatively simple rules: a lambda-calculus with explicit (dependent) types

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
;;; ### Let's play together at: <<<https://github.com/fredokun/LaTTe|||(lambda(x)t)>>>
;;; you're just a `lein new my-cool-maths-in-clojure` away...
;; (no? really? how unfortunate :~( )


;;; # Thank you!

