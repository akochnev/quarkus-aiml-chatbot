# build chat bot - native
oc new-build quay.io/quarkus/ubi-quarkus-native-s2i:19.3.1-java8~https://github.com/akochnev/quarkus-hackfest-apr2020 --context-dir=chatbot --name=hackfest-chatbot
oc patch bc/hackfest-chatbot -p '{"spec":{"resources":{"limits":{"cpu":"4", "memory":"6Gi"}}}}'
oc start-build hackfest-chatbot

# build processor - native
oc new-build quay.io/quarkus/ubi-quarkus-native-s2i:19.3.1-java8~https://github.com/akochnev/quarkus-hackfest-apr2020 --context-dir=sentiment-processor --name=processor-s2i
oc patch bc/processor-s2i -p '{"spec":{"resources":{"limits":{"cpu":"4", "memory":"6Gi"}}}}'
kn service create processor-s2i-2 --image image-registry.openshift-image-registry.svc.cluster.local:5000/akuser3-dev/processor-s2i 