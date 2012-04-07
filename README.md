## Description

This is just a silly Web application to display various data compiled from the four Rambo movies.
I cannot claim ownership of this data; I have taken it from the following Web site: http://www.geekstir.com/rambos-kill-stats.
Nonetheless, I wanted an excuse to take Compojure and Incanter for a spin, and after coming across this site one day,
I thought, "AHA!", here is an opportunity.

## Getting it running locally

This project uses Leiningen, version 1.5.2 or greater, so you will need to get it in order to run this locally.
After downloading the project, run the following steps:

lein deps
lein uberjar
lein run

The last step will launch Jetty; point your browser to http://localhost:8080 and you should see the main page.