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

(def sample-form
  (html-doc "Kill Statistics in the four Rambo movies"
    (form-to [:get "/kills-with-shirt-on"]
      (submit-button "Kills with shirt on"))))

(defn foo []
  ; Note that the current docos on read-dataset appear to be wrong;
  ; even though it states that it can take either a filename or a URL,
  ; the latter results in a ClassCastException.
  (let [data (read-dataset (.getFile (clojure.java.io/resource "kills-with-shirt-on.csv")) :header true)
        x (sel data :cols 0)
        log-x (map #(Math/log %) x)
        y (sel data :cols 1)
        log-y (map #(Math/log %) y)
        loglog-lm (linear-model log-y log-x :intercept false)
        equation (str "k(n)=n^{" (:coefs loglog-lm) "}")
        plot
          (scatter-plot
            :movie-num
            :kills
            :data data
            :x-label "Movie number"
            :y-label "# of kills")
        cross (ShapeUtilities/createDiagonalCross 3 1)
        out-stream (ByteArrayOutputStream.)
        header {:status 200
                :headers {"Content-Type" "image/png"}}]
    (do
      (doto
        plot
        (add-function #(Math/pow % (:coefs loglog-lm)) 1 4)
        (add-latex 2.5 50 equation))
      (save plot out-stream)
      (ByteArrayInputStream. (.toByteArray out-stream)))))

(defroutes webservice
  (GET "/" [] sample-form)
  (GET "/kills-with-shirt-on" [] (foo)))

(defn -main [& args]
  (run-jetty (var webservice) {:port 8080}))
