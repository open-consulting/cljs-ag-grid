(ns rum-ag-grid.core
  (:require
   [cljs.core.async :as async]
   [goog.html.legacyconversions :as conv]
   [goog.net.jsloader :as jsloader]
   [rum.core :as rum])
  (:require-macros
   [cljs.core.async.macros :refer [go]]
   [hiccups.core :as hiccups]))

(defn load-script-async
  [uri]
  (let [ch (async/promise-chan)
        trusted-uri (->> uri str conv/trustedResourceUrlFromString)]
    (.addCallback (jsloader/safeLoad trusted-uri)
                  #(async/put! ch :lib-loaded)) ch))

(def ^:const lib-uri "//cdnjs.cloudflare.com/ajax/libs/ag-grid/18.1.1/ag-grid.min.js")

(defn lib-loaded?
  []
  js/window.agGrid)

(defn load-lib
  []
  (load-script-async lib-uri))

(defn get-value
  [params]
  (let [{:keys [value]} (js->clj params :keywordize-keys true)]
    value))

(def ag-grid-default-ui
  {:ag-grid-el nil
   :grid-opts nil})

(defn create-opts
  "Common usecase of opts is to pass events-chan to col-defs"
  ([col-defs row-data]
   (create-opts col-defs row-data {}))

  ([col-defs row-data {:keys [onRowClicked enableFilter?] :as opts
                       :or {enableFilter? false}}]
   (let [col-defs* (if (fn? col-defs) (col-defs opts) col-defs)]
     {:columnDefs col-defs*
      :enableFilter enableFilter?
      :enableSorting true
      :rowSelection "single"
      :rowData row-data
      :pagination true
      :paginationPageSize 10
      :onRowClicked (fn [e] (when onRowClicked (onRowClicked e)))
      :onGridReady
      (fn [params]
        (.. params -api sizeColumnsToFit))})))

(defn mixin
  [col-defs]
  {:will-update
   (fn [state]
     (let [{:keys [data]} (-> state :rum/args first)
           {:keys [ag-grid-ui]} state]
       (swap! ag-grid-ui assoc :data data)
       state))

   :did-mount
   (fn [state]
     (let [el (rum/dom-node state)
           {:keys [data] :as props} (-> state :rum/args first)
           *ui* (atom ag-grid-default-ui)]
       (go (when-not (lib-loaded?)
             (async/<! (load-lib)))
           (let [grid-opts (clj->js (create-opts col-defs data props))
                 ag-grid-el (js/agGrid.Grid. el grid-opts)]
             (swap! *ui* assoc
               :ag-grid-el ag-grid-el
               :grid-opts grid-opts)))
       (add-watch *ui* :data
                  (fn [_ _ _ {:keys [ag-grid-el grid-opts]}]
                    (when (not= ag-grid-el nil)
                      (let [data (-> (js->clj grid-opts) (get "rowData"))]
                        (.setRowData (.-api grid-opts) (clj->js data))))))
       (assoc state :ag-grid-ui *ui*)))})
