(ns rum-ag-grid.example
  (:require
   [rum.core :as rum]
   [rum-ag-grid.core :as ag-grid]
   [rum-ag-grid.example-data :as data]))

(enable-console-print!)

(def col-defs
  [{:headerName "ID"
    :field "id"}
   {:headerName "Name"
    :field "name"}
   {:headerName "Username"
    :field "username"}
   {:headerName "E-mail"
    :field "email"}
   {:headerName "Phone"
    :field "phone"}
   {:headerName "Website"
    :field "website"}
   ])

(rum/defc grid-ui <
  (ag-grid/mixin col-defs)
  [{:keys [height] :or {height "500px"}}]
  [:.ag-theme-fresh.ag-theme-ico
   {:style {:height height}}])

(rum/defc ui <
  []
  [:div
   (grid-ui {:data data/users})])

(defn init
  []
  (rum/mount (ui)  (js/document.getElementById "app")))

(init)
