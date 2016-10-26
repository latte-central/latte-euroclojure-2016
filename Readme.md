
LaTTe @ euroclojure 2016
========================

This is the repository of the talk about LaTTe at
the euroclojure 2016 conference...

It is an emacs-based **livecoding** presentation. To see
the slides, you'll need:

  - emacs 
  - clojure 1.8+ (obviously)
  - cider
  - LaTTe (only if you want to livecode ...)
  - smartparens (probably optional)
  - maybe some other dependencies (please fill an issue...).

To start with the slides with holes (to fill):

```
sh run-emacs.sh src/latte_euroclojure_2016/core.clj &
```
(you'll need to find the and probably change the key bindings...)

And if you prefer the slides with solutions:

```
sh run-emacs.sh src/latte_euroclojure_2016/full.clj &
```

**Remark**: maybe it's simpler to wait for the video...

----
The content is (C) 2016 Frederic Peschanski CC-BY-SA 4.0
except for the `live-clojure-talks.el` file, a clojure tweak for 
the `live-code-talks.el` package by David Christensen 
(cf. https://github.com/david-christiansen/live-code-talks)
... under the GPL3.
