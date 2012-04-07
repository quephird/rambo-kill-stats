(use '(incanter io core datasets charts stats latex))
(import org.jfree.util.ShapeUtilities)

(def data 
  (read-dataset "kills-with-shirt-on.csv" :header true))

(def x (sel data :cols 0))   
(def y (sel data :cols 1))
(def log-x (map #(Math/log %) x))
(def log-y (map #(Math/log %) y))
;(def sqrt-y (map #(Math/sqrt %) y))

(def plot
  (scatter-plot 
    :movie-num
    :kills
    :data data
    :x-label "Movie number"
    :y-label "# of kills"))
 
(def cross (ShapeUtilities/createDiagonalCross 3 1))
(.setSeriesShape (.getRenderer (.getPlot plot)) 0 cross)

;(view plot)
;
;(def log-plot
;  (scatter-plot
;    x
;    log-y
;    :x-label "Movie number"
;    :y-label "Log of # of kills"))
;
;(view log-plot)
;
;(def sqrt-lm (linear-model sqrt-y x :intercept true))
;
;(def sqrt-plot
;  (scatter-plot
;    x
;    sqrt-y
;    :x-label "Movie number"
;    :y-label "Square root of # of kills"))
;
;(doto
;  sqrt-plot
;  (add-lines x (:fitted sqrt-lm))
;  view)
;
;(def loglog-plot
;  (scatter-plot
;    log-x
;    log-y
;    :x-label "Log of movie number"
;    :y-label "Log of # of kills"))

(def loglog-lm (linear-model log-y log-x :intercept false))

(def eq (str "k(n)=n^{" (:coefs loglog-lm) "}"))

;(doto
;  loglog-plot
;  (add-lines log-x (:fitted loglog-lm))
;  view)

(doto
  plot
  (add-function #(Math/pow % (:coefs loglog-lm)) 1 4)
  (add-latex 2.5 50 eq)
  view)
