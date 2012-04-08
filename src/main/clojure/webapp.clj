(ns webapp
  (:gen-class)
  (:use [compojure core response]
        [incanter io charts core latex datasets stats ]
        [hiccup core form page]
        [ring.adapter jetty])
  (:import (java.io ByteArrayOutputStream
                    ByteArrayInputStream)
           org.jfree.util.ShapeUtilities))

(defn html-doc
  [title & body]
  (html
    (doctype :html4)
    [:html
      [:head
        [:title title]]
      [:body
       [:div
         [:h2 "Various statistics from the four Rambo movies"	 ]]
        body]]))

(def *data* (atom []))

(defn- load-data []
  (reset! *data*
    (read-dataset (.getFile (clojure.java.io/resource "rambo-kill-stats.csv")) :header true)))

(def sample-form
  (html-doc "Kill Statistics in the four Rambo movies"
    (form-to [:get "/kills-with-shirt-on"]
      (submit-button "Kills with shirt on"))))

(defn kills-with-shirt-on []
  ; Note that the current docos on read-dataset appear to be wrong;
  ; even though it states that it can take either a filename or a URL,
  ; the latter results in a ClassCastException.
  (let [x (sel @*data* :cols 0)
        log-x (map #(Math/log %) x)
        y (sel @*data* :cols 1)
        log-y (map #(Math/log %) y)
        loglog-lm (linear-model log-y log-x :intercept false)
        equation (str "k_{SHIRT\\_ON}(n)=n^{" (:coefs loglog-lm) "}")
        plot
          (scatter-plot
            x y
            :x-label "Movie number"
            :y-label "# of kills")
        cross (ShapeUtilities/createDiagonalCross 3 1)
        out-stream (ByteArrayOutputStream.)]
    (do
      (doto
        plot
        (add-function #(Math/pow % (:coefs loglog-lm)) 1 5)
        (add-latex 3 125 equation))
      (save plot out-stream)
      (ByteArrayInputStream. (.toByteArray out-stream)))))

(defn kills-with-shirt-off []
  (let [x (sel @*data* :cols 0)
        y (sel @*data* :cols 2)
        plot
          (scatter-plot
            x y
            :x-label "Movie number"
            :y-label "# of kills")
        cross (ShapeUtilities/createDiagonalCross 3 1)
        out-stream (ByteArrayOutputStream.)]
    (do
      (save plot out-stream)
      (ByteArrayInputStream. (.toByteArray out-stream)))))

(defn kills-by-rambo []
  (let [x (sel @*data* :cols 0)
        y (map + (sel @*data* :cols 1) (sel @*data* :cols 2))
        plot
          (scatter-plot
            x y
            :x-label "Movie number"
            :y-label "# of kills")
        cross (ShapeUtilities/createDiagonalCross 3 1)
        out-stream (ByteArrayOutputStream.)]
    (do
      (save plot out-stream)
      (ByteArrayInputStream. (.toByteArray out-stream)))))

(defroutes webservice
  (GET "/" [] sample-form)
  (GET "/kills-by-rambo" []
    {:status 200
     :headers {"Content-Type" "image/png"}
     :body (kills-by-rambo)})
  (GET "/kills-with-shirt-off" []
    {:status 200
     :headers {"Content-Type" "image/png"}
     :body (kills-with-shirt-off)})
  (GET "/kills-with-shirt-on" []
    {:status 200
     :headers {"Content-Type" "image/png"}
     :body (kills-with-shirt-on)}))

(defn -main [& args]
  (do
    (load-data)
    (run-jetty (var webservice) {:port 8080})))
