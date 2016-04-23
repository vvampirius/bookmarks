package bookmark

import (
	"errors"
	"io/ioutil"
	"log"
	"os"
	"path"
	"strings"
)

type Bookmark struct {
	Path string
}

func (bookmark Bookmark) Exists() bool {
	exist := false
	if info, err := os.Stat(bookmark.Path); err != nil {
		log.Println(err)
	} else if info.IsDir() {
		exist = true
	}
	return exist
}

func (bookmark Bookmark) CreateIfNotExists() error {
	var err error
	if bookmark.Exists() == false {
		err = os.Mkdir(bookmark.Path, 0700)
	}
	return err
}

func (bookmark Bookmark) Url() string {
	url := ""
	if bookmark.Exists() {
		if data, err := ioutil.ReadFile(path.Join(bookmark.Path, "url")); err == nil {
			url = strings.SplitN(string(data), "\n", 2)[0]
		} else {
			log.Println(err)
		}
	}
	return url
}

func (bookmark Bookmark) SetUrl(url string) error {
	return ioutil.WriteFile(path.Join(bookmark.Path, "url"), []byte(url), 0600)
}

func New(url string, p string, createIfNotExists bool) (Bookmark, error) {
	b := Bookmark{}
	if len(p) == 0 {
		return b, errors.New("path is empty!")
	}
	if len(url) > 0 {
		if len(url) <= 3 {
			return b, errors.New("url is too short!")
		}
		p = path.Join(p, GetMD5Hash(url))
	}
	b.Path = p
	if createIfNotExists {
		if err := b.CreateIfNotExists(); err != nil {
			return b, err
		}
	}
	if b.Exists() == false {
		return b, errors.New(url + " doesn't exists!")
	}
	return b, nil
}
