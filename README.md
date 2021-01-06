# CorDapp Samples - Kotlin
<p align="center">
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">
</p>

## Introduction
This repository contains a sample app IOU copied from r3.

https://github.com/corda/samples-kotlin/tree/master/Basic/cordapp-example

## How to run
You will be able to start the nodes and issue IOU with PartyA through below steps

./gradlew deployNodes
./gradlew build
./build/nodes/runnodes
./gradlew runPartyAServer
create a POST request to localhost:10050 with json body:

``
{
	"lender" : "O=PartyB, L=New York, C=US", 
	"value" : 2
}
``
