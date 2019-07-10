<h1 align="center">Welcome to Strimzi/AMQ Streams Demo 👋</h1>
<p>
  <a href="https://twitter.com/systemcraftsman">
    <img alt="Twitter: systemcraftsman" src="https://img.shields.io/twitter/follow/systemcraftsman.svg?style=social" target="_blank" />
  </a>
</p>

> Hands-on demo material for Strimzi/AMQ Streams originally created by @scholzj

Steps:

> //TODO: add each step's desc.

`sed -i '' 's/namespace: .*/namespace: kafka-demo/' 01-install-cluster-operator/*RoleBinding*.yaml`

`oc apply -f ./01-install-cluster-operator`

`oc apply -f 02-simplest-cluster.yaml`

`oc apply -f 03-with-config.yaml`

`oc get pods -w`

`oc apply -f 04-full.yaml`

`oc apply -f 05-scale.yaml`

`oc get pods -w`

`oc apply -f 06-with-eo.yaml`

`oc get pods -w`

`oc apply -f 07-my-topic.yaml`

`oc get kafkatopics`

`oc exec -it my-cluster-zookeeper-0 -- bin/kafka-topics.sh --zookeeper localhost:21810 --list`

`oc exec -it my-cluster-zookeeper-0 -- bin/kafka-topics.sh --zookeeper 127.0.0.1:21810 --topic my-native-topic --create --partitions 3 --replication-factor 2`

`oc get kafkatopics`

`oc apply -f 09-demo-application.yaml`

`oc apply -f 08-my-user.yaml`

- Twitter: [@systemcraftsman](https://twitter.com/systemcraftsman)
- Github: [@mabulgu](https://github.com/mabulgu)
