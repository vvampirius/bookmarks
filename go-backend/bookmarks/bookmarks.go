package bookmarks

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/vvampirius/bookmarks/go-backend/bookmarks/bookmark"
	"io/ioutil"
	"net/http"
	"path"
	"strings"
)

type Bookmarks struct {
	Path string
}

func (bookmarks Bookmarks) CheckPassword(fileName string, password string) (bool, error) {
	data, err := ioutil.ReadFile(path.Join(bookmarks.Path, fileName))
	if err == nil {
		if password != strings.SplitN(string(data), "\n", 2)[0] {
			fmt.Println("check: " + password)
			fmt.Println("file: " + strings.SplitN(string(data), "\n", 2)[0])
			return false, nil
		}
	}
	return true, err
}

func (bookmarks Bookmarks) Get(url string, password string) (bookmark.Bookmark, error) {
	if len(url) == 0 {
		return bookmark.Bookmark{}, errors.New("no url")
	}
	if passwordCheck, _ := bookmarks.CheckPassword("get.password", password); passwordCheck == false {
		return bookmark.Bookmark{}, errors.New("Access Forbidden!")
	}
	b, err := bookmark.New(url, bookmarks.Path, false)
	return b, err
}

func (bookmarks Bookmarks) Add(url string, password string) (bool, error) {
	if len(url) == 0 {
		return false, errors.New("no url")
	}
	if passwordCheck, _ := bookmarks.CheckPassword("add.password", password); passwordCheck == false {
		return false, errors.New("Access Forbidden!")
	}
	added := false
	var err error
	if _, err = bookmark.New(url, bookmarks.Path, false); err != nil {
		b := bookmark.Bookmark{}
		if b, err = bookmark.New(url, bookmarks.Path, true); err == nil {
			if err = b.SetUrl(url); err == nil {
				added = true
			}
		} else {
			fmt.Println(err.Error())
		}
	} else {
		err = errors.New("Already Exists!")
	}
	return added, err
}

func (bookmarks Bookmarks) List(password string) ([]bookmark.Bookmark, error) {
	var bs []bookmark.Bookmark
	if passwordCheck, _ := bookmarks.CheckPassword("list.password", password); passwordCheck == false {
		return bs, errors.New("Access Forbidden!")
	}
	if files, err := ioutil.ReadDir(bookmarks.Path); err == nil {
		for _, file := range files {
			if len(file.Name()) == 32 {
				if b, err := bookmark.New("", path.Join(bookmarks.Path, file.Name()), false); err == nil {
					bs = append(bs, b)
				}
			}
		}
	}
	return bs, nil
}

func (bookmarks Bookmarks) HttpGet(w http.ResponseWriter, r *http.Request) {
	var url, password string
	r.ParseForm()
	for k, v := range r.Form {
		if k == "url" {
			url = string([]rune(v[0]))
		}
		if k == "password" {
			password = string([]rune(v[0]))
		}
	}
	if b, err := bookmarks.Get(url, password); err == nil {
		if j, err := json.Marshal(b.Bookmark()); err == nil {
			w.Header().Set("Content-Type", "application/json")
			fmt.Fprintln(w, string(j))
		} else {
			w.WriteHeader(http.StatusBadGateway)
			fmt.Fprintln(w, err.Error())
		}
	} else if err.Error() == "Access Forbidden!" {
		w.WriteHeader(http.StatusForbidden)
		fmt.Fprintln(w, err.Error())
	} else {
		w.WriteHeader(http.StatusNotFound)
		fmt.Fprintln(w, err.Error())
	}
}

func (bookmarks Bookmarks) HttpAdd(w http.ResponseWriter, r *http.Request) {
	var url, password string
	r.ParseForm()
	for k, v := range r.Form {
		if k == "url" {
			url = string([]rune(v[0]))
		}
		if k == "password" {
			password = string([]rune(v[0]))
		}
	}
	if b, err := bookmarks.Add(url, password); b && err == nil {
		fmt.Fprintln(w, "ADDED")
	} else if err.Error() == "Access Forbidden!" {
		w.WriteHeader(http.StatusForbidden)
		fmt.Fprintln(w, err.Error())
	} else {
		w.WriteHeader(http.StatusBadGateway)
		fmt.Fprintln(w, err.Error())
	}
}

func (bookmarks Bookmarks) HttpList(w http.ResponseWriter, r *http.Request) {
	var password string
	r.ParseForm()
	for k, v := range r.Form {
		if k == "password" {
			password = string([]rune(v[0]))
		}
	}
	if bs, err := bookmarks.List(password); err == nil {
		var bst []bookmark.BookmarkType
		for _, b := range bs {
			bst = append(bst, b.Bookmark())
		}
		if j, err := json.Marshal(bst); err == nil {
			w.Header().Set("Content-Type", "application/json")
			fmt.Fprintln(w, string(j))
		} else {
			w.WriteHeader(http.StatusBadGateway)
			fmt.Fprintln(w, err.Error())
		}
	} else if err.Error() == "Access Forbidden!" {
		w.WriteHeader(http.StatusForbidden)
		fmt.Fprintln(w, err.Error())
	} else {
		w.WriteHeader(http.StatusBadGateway)
		fmt.Fprintln(w, err.Error())
	}
}
