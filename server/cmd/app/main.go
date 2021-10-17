package main

import (
	"fmt"
	"net"
	"os"
)

type SensorData struct {
	Name  string `json:"name"`
	Value int    `json:"value"`
}

func main() {
	addr := net.UDPAddr{
		Port: 8080,
		IP:   net.ParseIP("0.0.0.0"),
	}

	conn, err := net.ListenUDP("udp", &addr)
	if err != nil {
		fmt.Println("listening failed")
		os.Exit(-1)
	}
	defer conn.Close()

	p := make([]byte, 1024)

	for {
		n, remoteAddr, err := conn.ReadFromUDP(p)
		if err != nil {
			fmt.Println("Some error happened:", err)
		}
		// Write to db or file
		fmt.Printf("Read a message from %v %s\n", remoteAddr, string(p[:n]))
	}
}
