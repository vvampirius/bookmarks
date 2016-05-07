package main

import "github.com/vvampirius/bookmarks/go-backend/bookmarks"
import "os"
import "net/http"
import "log"
import "flag"

func main() {
	listen := flag.String("l", ":9090", "listen [<IP>]:<port>")
	flag.Parse()
	dir := flag.Arg(0)
	if dir == "" {
		log.Fatal("You must specify directory with bookmarks!")
	}
	if _, err := os.Stat(dir); os.IsNotExist(err) {
		log.Fatal(err)
	}
	if dirStat, _ := os.Stat(dir); dirStat.IsDir() != true {
		log.Fatal(dir + " is not directory!")
	}
	bs := bookmarks.Bookmarks{dir}
	http.HandleFunc("/get/", bs.HttpGet)
	http.HandleFunc("/add/", bs.HttpAdd)
	http.HandleFunc("/list/", bs.HttpList)
	err := http.ListenAndServe(*listen, nil) // set listen port
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}
