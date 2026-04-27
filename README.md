## 🛠 Prerequisites Installation

### Java (JDK 17)

# Manual Java configuration on Linux

```bash
. /etc/profile
sudo update-alternatives --install "/usr/bin/java" "java" "/opt/java/jdk-17.0.10/bin/java" 1
sudo update-alternatives --install "/usr/bin/javac" "javac" "/opt/java/jdk-17.0.10/bin/javac" 1
sudo update-alternatives --set java /opt/java/jdk-17.0.10/bin/java
sudo update-alternatives --config java
java --version
source /etc/profile.d/java.sh
source /etc/profile.d/maven.sh
```

### Environment Variables

Create `/etc/profile.d/java.sh`:

```bash
JAVA_HOME=/opt/java/jdk-17.0.10
PATH=$PATH:$HOME/bin:$JAVA_HOME/bin
export JAVA_HOME
export PATH
```

Then set permissions:

```bash
sudo chmod 755 /etc/profile.d/java.sh
```

### Maven

Create `/etc/profile.d/maven.sh`:

```bash
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
```

Set permissions:

```bash
sudo chmod 755 /etc/profile.d/maven.sh
```

## 🔧 Useful Commands

### File Transfer (SCP)

```bash
# Transfer backup.sql to remote server
scp backup.sql mmad@10.10.10.1:/home/admin/Downloads
```

### SSH Tunneling

```bash
# Establish reverse SSH tunnel (remote port 8080 → local port 20)
ssh -R 8080:localhost:20 mmad@10.10.10.1
```

## 🗄️ PostgreSQL with Docker

### Volume and Network Setup

```bash
# Create volume for persistent data
sudo docker volume create pg-data

# Create network for backend-database communication
sudo docker network create pg-net
```

### Run PostgreSQL Container

```bash
# Start PostgreSQL instance
sudo docker run --name pg \
  --volume pg-data:/var/lib/postgresql/data \
  --network pg-net \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:latest
```

### Database Operations

```bash
# Access PostgreSQL CLI
sudo docker exec -it pg psql -U postgres

# Restore backup into 'version' database
cat bu.sql | sudo docker exec -i pg psql -U postgres -d version
```

### SQL Commands (run inside psql)

```sql
-- Create superuser
CREATE USER cloud WITH SUPERUSER PASSWORD 'cloud';
```

## 📡 Kafka Setup

### Clone and Run

```bash
# Download Kafka stack
git clone https://github.com/conduktor/kafka-stack-docker-compose.git
cd kafka-stack-docker-compose

# Start Kafka single-node cluster
sudo docker-compose -f zk-single-kafka-single.yml up -d

# Verify running containers (kafka1, zoo1)
sudo docker ps | grep -E "(kafka1|zoo1)"

# Access Kafka container shell
sudo docker exec -it kafka1 /bin/bash
```

### Management Commands

```bash
# Stop Kafka cluster
sudo docker-compose -f zk-single-kafka-single.yml down

# Check Kafka version
kafka-topics --version

# Consume messages from terminal
kafka-console-consumer --topic oauth-topic \
  --from-beginning \
  --bootstrap-server localhost:9092
```

## 🚀 Running Java Applications with Jasypt

```bash
# Run with encrypted configuration (Jasypt)
java -Djasypt.encryptor.password=MASTER_KEY -jar your-application.jar
```

## 📊 Container Management

```bash
# Formatted container listing
sudo docker ps --format \
"ID: {{.ID}}\nImage: {{.Image}}\nPorts: {{.Ports}}\nStatus: {{.Status}}\nNames: {{.Names}}\n-----------------------"
```

## 🐳 Common Docker Images

### Security Testing

```bash
# Kali Linux (penetration testing)
sudo docker run --network pen-net -h attacker -it --name kalibox kalilinux/kali-rolling

# Metasploitable2 (vulnerable VM)
sudo docker run --network pen-net -h private -it --name metasploitable2 tleemcjr/metasploitable2

# Broken Web App (bWAPP)
sudo docker run -d -p 80:80 --name bwp hackersploit/bwapp-docke
```

### Identity & Access

```bash
# Keycloak (Identity Server)
docker run -d --name keycloak -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:24.0.1 start-dev
```

### Version Control

```bash
# Gitea (Git Server)
docker run -d --name gitea \
  -e USER_UID=1000 -e USER_GID=1000 \
  -p 3000:3000 -p 222:22 \
  -v /home/dev/Documents/Docker/gitea_data:/data \
  -v /etc/timezone:/etc/timezone:ro \
  -v /etc/localtime:/etc/localtime:ro \
  gitea/gitea:latest
```

### Data Stores

```bash
# Redis (Cache/Message Broker)
sudo docker run -d --name redis -p 6379:6379 redis:latest --appendonly yes

# Redis Stack (with GUI)
docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 \
  -e REDIS_ARGS="--appendonly yes" \
  redis/redis-stack:latest

# Redis CLI Operations
docker exec -it redis redis-cli
# List all keys: keys *
# Get specific key: get <key_name>
```

### Artifact Repository

```bash
# Nexus (Artifact Repository)
# Set permissions first
sudo docker run --rm -v /home/dev/Documents/Docker/nexus_data:/nexus-data \
  alpine sh -c "chown -R 200:200 /nexus-data"

# Run Nexus
sudo docker run -d -p 7900:8081 --name nexus \
  --dns 8.8.8.8 --dns 4.2.2.4 \
  --dns 178.22.122.100 --dns 185.51.200.2 \
  -v /home/dev/Documents/Docker/nexus_data:/nexus-data \
  sonatype/nexus3:latest

# Get initial admin password
sudo docker exec nexus cat /nexus-data/admin.password
```
