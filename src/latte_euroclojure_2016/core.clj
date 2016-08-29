
;;; # Live-coding Mathematics: your first Clojure proof

;;; ## (LaTTe@Euroclojure 2016)



;;; # Let's begin ...

(ns latte-euroclojure-2016.core
  "This is a talk about LaTTe given @ Euroclojure 2016."

  (:refer-clojure :exclude [and or not])

  (:require [latte.core :as latte
             :refer [definition defthm defaxiom defnotation
                     forall lambda ==>
                     assume have proof
                     term type-of check-type?]])

  (:require [latte.quant :as q :refer [exists]])

  (:require [latte.prop :as p :refer [<=> and or not]])

  (:require [latte.equal :as eq :refer [equal]])
  )

