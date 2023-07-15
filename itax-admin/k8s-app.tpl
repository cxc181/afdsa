apiVersion: v1
data:
  .dockerconfigjson: {K8S_REGISTRY_CONFIG}
kind: Secret
metadata:
  name: harborsecret
  namespace: {K8S_NAMESPACE}
type: kubernetes.io/dockerconfigjson
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  namespace: {K8S_NAMESPACE}
  name: longhorn-{APP_NAME}-pvc
spec:
  accessModes:
    - ReadWriteOnce
  storageClassName: longhorn
  resources:
    requests:
      storage: 5Gi
---
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: {K8S_NAMESPACE}
  name: {APP_NAME}-deployment
  labels:
    app: {APP_NAME}
spec:
  replicas: 3
  selector:
    matchLabels:
      app: {APP_NAME}
  template:
    metadata:
      labels:
        app: {APP_NAME}
    spec:
      imagePullSecrets:
      - name: harborsecret
      containers:
      - name: filebeat-{APP_NAME}
        image: harbor.db-inc.com/dbops/filebeat:6.x
        env:
        - name: INDEX_PREFIX
          value: "k8s-{APP_NAME}-%{+YYYY.MM.dd}"
        volumeMounts:
        - name: app-logs
          mountPath: /log/{APP_NAME}
        - name: filebeat-config
          mountPath: /etc/filebeat
      - name: {APP_NAME}
        image: {IMAGE_URL}:{IMAGE_TAG}
        imagePullPolicy: IfNotPresent
        ports:
        - name: httpport
          containerPort: 8080
        env:
        - name: dbops_logs_catalina
          value: "stdout"
        - name: dbops_logs_catalina_tags
          value: "stage={PROJECT_ENV},app={APP_NAME}"
        volumeMounts:
        - name: app-logs
          mountPath: /app/logs/{APP_NAME}
      volumes:
      - name: app-logs
        persistentVolumeClaim:
          claimName: longhorn-{APP_NAME}-pvc
      - name: filebeat-config
        configMap:
          name: filebeat-config
          items:
          - key: filebeat.yml
            path: filebeat.yml
---
apiVersion: v1
kind: Service
metadata:
  namespace: {K8S_NAMESPACE}
  name: {APP_NAME}-service
spec:
  type: NodePort
  selector:
    app: {APP_NAME}
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
---
apiVersion: v1
kind: ConfigMap
metadata:
  namespace: {K8S_NAMESPACE}
  name: filebeat-config
data:
  filebeat.yml: |-
    filebeat.prospectors:
    - input_type: log
      tags: ["k8s-{APP_NAME}"]
      paths:
        - "/log/{APP_NAME}/*.log"
    output.logstash:
      hosts: ["172.16.100.208:5044"]

#---
#apiVersion: extensions/v1beta1
#kind: Ingress
#metadata:
#  namespace: {K8S_NAMESPACE}
#  name: {APP_NAME}-ingress
#  annotations:
#    kubernetes.io/ingress.class: "traefik"
#    ingress.kubernetes.io/ssl-redirect: "false"  
#    traefik.frontend.rule.type: "PathPrefixStrip"
#    traefik.ingress.kubernetes.io/frontend-entry-points: "http,https"
#    traefik.ingress.kubernetes.io/priority: "3"
#spec:
#  rules:
#  - host: k8s.cwl.cn
#    http:
#      paths:
#      - path: /cwl/*
#        backend:
#          serviceName: {APP_NAME}-service
#          servicePort: 80
