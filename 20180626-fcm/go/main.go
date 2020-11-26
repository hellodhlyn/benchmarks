package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"os"
	"time"

	"golang.org/x/oauth2/google"

	"firebase.google.com/go"
	"firebase.google.com/go/messaging"
	"google.golang.org/api/option"
)

type Payload struct {
	Message string `json:"message"`
	Token   string `json:"token"`
}

const (
	fcmScope = "https://www.googleapis.com/auth/firebase.messaging"
)

var (
	ctx = context.Background()
)

func send(client *messaging.Client, idx int, c chan int) {
	if idx%20 == 0 {
		fmt.Println(idx)
	}

	// Deserialize json payload
	raw := "{\"message\":\"Message Body\",\"token\":\"ExampleToken\"}"
	var payload Payload
	_ = json.Unmarshal([]byte(raw), &payload)

	// Request FCM message API.
	message := &messaging.Message{
		Data: map[string]string{
			"key": payload.Message,
		},
		Token: payload.Token,
	}

	res, _ := client.SendDryRun(ctx, message)
	fmt.Println(res)

	c <- idx
}

func main() {
	cred, _ := google.CredentialsFromJSON(ctx, []byte(os.Getenv("FIREBASE_SERVICE_ACCOUNT")), fcmScope)
	opt := option.WithCredentials(cred)
	app, err := firebase.NewApp(ctx, nil, opt)
	if err != nil {
		log.Fatal("Failed to create app: ", err)
	}

	client, err := app.Messaging(ctx)
	if err != nil {
		log.Fatal("Failed to initialized client: ", err)
	}

	startTime := time.Now()

	c := make(chan int)
	for i := 1; i <= 5000; i++ {
		go send(client, i, c)
	}

	for i := 1; i <= 5000; i++ {
		_ = <-c
	}

	fmt.Printf("Elapsed Time : %d ms\n", time.Now().Sub(startTime)/time.Millisecond)
}
