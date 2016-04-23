package main

//import "fmt"
import "github.com/vvampirius/bookmarks/go-backend/bookmarks"

//import "github.com/vvampirius/bookmarks/go-backend/bookmarks/bookmark"

//import "os"
//import "path"
import "net/http"
import "log"

func main() {
	bs := bookmarks.Bookmarks{"/tmp"}
	http.HandleFunc("/get/", bs.HttpGet)
	http.HandleFunc("/add/", bs.HttpAdd)
	err := http.ListenAndServe(":9090", nil) // set listen port
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}
