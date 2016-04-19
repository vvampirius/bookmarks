package bookmark

import (
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
