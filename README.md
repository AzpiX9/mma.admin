# Mma.Admin

> Ce fichier est à compléter par vos soins. Les endroits à compléter sont signalés par des **TODO**

## Description

`Mma.Admin` est une application console pour administrer les occupations de salles de réunion. Elle permet, entre autres choses, de réserver des créneaux et de chercher des disponibilités.

L'application est à déployer sur les postes des membres du personnel responsables d'administrer les salles de réunion.

## Développer le projet

### Prérequis

Vous devez avoir le JDK 21 installé sur votre machine. Pour récupérer le projet, il est préférable d'avoir un client `git` installé. Enfin, nous vous recommandons d'utiliser un EDI comme IntelliJ ou Eclipse.

- rooms.csv
- users.csv
- services.csv

> **NOTE**
> 
> C:calendars\rooms.csv
> C:calendars\users.csv
> C:calendars\services.csv

### Construction

```bash
# Clonez le dépôt
> git clone https://git.helmo.be/students/info/q210138/mma.admin

> cd [le chemin vers le dépôt local] # Accédez au répertoire du projet
> ./gradlew restore # Installez les dépendances 
> ./gradlew build # Construisez le projet
> ./gradlew test # Exécutez les tests unitaires
```

## Utilisation
*Avec des fichiers*
```bash
./gradlew run --args="--dir='src/main/resources/calendars/'"
```
*Avec une DB*
```bash
./gradlew run --args="--db='192.168.132.200:13306;user=Q210138;password=0138'"
```
