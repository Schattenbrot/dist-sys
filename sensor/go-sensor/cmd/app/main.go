package main

import (
	"encoding/json"
	"fmt"
	"net"
	"os"
	"strconv"
	"time"
)

type SensorData struct {
	Name  string `json:"name"`
	Value int    `json:"value"`
}

func sendData(data *SensorData, conn *net.UDPConn, c chan int) {
	newData := SensorData{
		Name:  data.Name,
		Value: data.Value + 1,
	}
	c <- newData.Value
	json, err := json.Marshal(newData)
	if err != nil {
		fmt.Println("failed to convert data to json.")
		return
	}
	fmt.Println(string(json[:]))

	conn.Write(json)

	// send to ... whatever the server addr and port is.
	// just needs to send some data to the DB, I guess.
}

func getSensorData() (*SensorData, error) {
	sensorData := &SensorData{}

	sensorData.Name = os.Getenv("SENSOR_NAME")
	if sensorData.Name == "" {
		sensorData.Name = "crazy-generic-sensor-name"
	}

	sensorValueString := os.Getenv("SENSOR_VALUE")
	sensorValue, err := strconv.Atoi(sensorValueString)
	if err != nil {
		return nil, err
	}
	sensorData.Value = sensorValue

	fmt.Println("sensorname:", sensorData.Name)
	fmt.Println("value:", sensorData.Value)

	return sensorData, nil
}

func setConnection() (*net.UDPConn, error) {
	addrs, err := net.LookupIP("server")
	if err != nil {
		fmt.Println("No 'server' dns.")
		return nil, err
	}
	fmt.Println(addrs)

	dst := net.UDPAddr{
		IP:   addrs[0],
		Port: 8080,
	}

	conn, err := net.DialUDP("udp", nil, &dst)
	if err != nil {
		fmt.Println("Something went wrong")
		return nil, err
	}
	return conn, nil
}

func main() {
	sensorData, err := getSensorData()
	if err != nil {
		fmt.Println("Error occurred:", err)
		return
	}

	conn, err := setConnection()
	if err != nil {
		fmt.Println("Error occurred:", err)
		return
	}
	defer conn.Close()

	d := time.NewTicker(2 * time.Second)

	interruptChannel := make(chan bool)
	go func() {
		time.Sleep(20 * time.Second)
		interruptChannel <- true
	}()

	c := make(chan int)
	for {
		select {
		// case <-ctx.Done():
		// 	fmt.Println("Done!")
		// 	return
		case <-interruptChannel:
			fmt.Println("Finished!")
			return
		case <-d.C:
			go sendData(sensorData, conn, c)
		}
		sensorData.Value = <-c
	}
}
