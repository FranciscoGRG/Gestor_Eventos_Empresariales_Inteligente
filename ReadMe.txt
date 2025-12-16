Proyecto de gestion de eventos empresariales con el siguiente stack:

Backend: Spring Boot
Frontend: React
Base de datos: MySQL
Despliegue: Kubernetes
Envio mails: MailTrap
Comunicacion interna: Kafka
Revision de codigo: SonarQube
Versionado: Git

Como desplegarlo:
1. Clonar el repositorio
2. Acceder a la carpeta frontend y ejecutar npm install
3. Acceder a la carpeta backend y ejecutar los siguientes comandos:
    - mvn clean install -DskipTests
    - docker-compose -f docker-compose.prod.yml build
    - kubectl apply -f k8s/
4. Verificar el estado de los pods con el comando: kubectl get pods -n event-manager
5. Una vez esten disponibles, hacer port forwading a frontend y api-gateway con los siguientes comandos:
    - kubectl port-forward svc/api-gateway 8082:8082 -n event-manager
    - kubectl port-forward svc/frontend 8080:80 -n event-manager
6. Acceder a http://localhost:8080 para ver el frontend
7. Si quieres auditar el codigo con sonar, tiene que ser desde local y no con el despliegue en k8s, cambiando el token de sonar para que funcione