(ns webapp
  (:gen-class)
  (:use [compojure core response route]
        [incanter io charts core latex datasets stats ]
        [hiccup core form page]
        [ring.adapter jetty])
  (:import (java.io ByteArrayOutputStream
                    ByteArrayInputStream)
           org.jfree.util.ShapeUtilities))

(def *data* (atom []))

(defn- determine-port-number []
  (let [env-port (System/getenv "PORT")]
    (if (nil? env-port)
      8080
      (Integer/parseInt env-port))))

(defn- load-data []
  (reset! *data*
    (read-dataset (.getFile (clojure.java.io/resource "rambo-kill-stats.csv")) :header true)))

(defn- write-to-out-stream
  [plot]
  (let [out-stream (ByteArrayOutputStream.)]
    (do
      (save plot out-stream)
      (ByteArrayInputStream. (.toByteArray out-stream)))))

(defn main-html []
  (html
    (doctype :html4)
    [:html
      [:head
       (include-css "./styles/rambo-kill-stats.css")
       (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js")
       (include-js "./js/rambo-kill-stats.js")
        [:title "Kill Statistics in the four Rambo movies"]]
      [:body
       [:div#content
         [:h2 "Kill statistics from the four Rambo movies"]]
       [:div#chart-container
         [:img#chart-image {:src ""}]]
       [:select {:id "chart-select"}
         [:option {:value "" :selected true} "Select a chart"]
         [:option {:value "./kills-with-shirt-on"} "Kills with shirt on"]
         [:option {:value "./kills-with-shirt-off"} "Kills with shirt off"]
         [:option {:value "./kills-by-rambo"} "Kills by Rambo irrespective of shirt status"]
         [:option {:value "./kills-by-accomplices"} "Kills by Rambo's accomplices"]
         [:option {:value "./kills-by-bad-guys"} "Kills by bad guys"]
         [:option {:value "./total-kills"} "Total guys killed"]
         ]]]))

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
        cross (ShapeUtilities/createDiagonalCross 3 1)]
    (write-to-out-stream
      (doto
        plot
        (add-function #(Math/pow % (:coefs loglog-lm)) 1 5)
        (add-latex 3 125 equation)))))

(defn kills-with-shirt-off []
  (let [x (sel @*data* :cols 0)
        y (sel @*data* :cols 2)
        plot
          (scatter-plot
            x y
            :x-label "Movie number"
            :y-label "# of kills")]
    (write-to-out-stream plot)))

(defn kills-by-rambo []
  (let [x (sel @*data* :cols 0)
        y (map + (sel @*data* :cols 1) (sel @*data* :cols 2))
        plot
          (scatter-plot
            x y
            :x-label "Movie number"
            :y-label "# of kills")]
    (write-to-out-stream plot)))

(defn kills-by-accomplices []
  (let [x (sel @*data* :cols 0)
        y (sel @*data* :cols 3)
        plot
          (scatter-plot
            x y
            :x-label "Movie number"
            :y-label "# of kills")]
    (write-to-out-stream plot)))

(defn kills-by-bad-guys []
  (let [x (sel @*data* :cols 0)
        y (sel @*data* :cols 4)
        plot
          (scatter-plot
            x y
            :x-label "Movie number"
            :y-label "# of kills")]
    (write-to-out-stream plot)))

(defn total-kills []
  (let [x (apply interleave (repeat 4 (sel @*data* :cols 0)))
        y (interleave (sel @*data* :cols 1) (sel @*data* :cols 2) (sel @*data* :cols 3) (sel @*data* :cols 4))
        groupings (flatten (repeat 4 ["Shirt on" "Shirt off" "Accomplices" "Bad guys"]))
        plot
          (stacked-bar-chart x y :legend true :group-by groupings)]
    (write-to-out-stream plot)))

(defroutes webservice
  (GET "/" [] (main-html))
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
     :body (kills-with-shirt-on)})
  (GET "/kills-by-accomplices" []
    {:status 200
     :headers {"Content-Type" "image/png"}
     :body (kills-by-accomplices)})
  (GET "/kills-by-bad-guys" []
    {:status 200
     :headers {"Content-Type" "image/png"}
     :body (kills-by-bad-guys)})
  (GET "/total-kills" []
    {:status 200
     :headers {"Content-Type" "image/png"}
     :body (total-kills)})
  (resources "/"))

(defn -main [& args]
  (do
    (load-data)
    (run-jetty (var webservice) {:port (determine-port-number)})))
