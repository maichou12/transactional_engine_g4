# Guide de mise en place de la base de données

Ce guide explique comment mettre en place la base de données avec toutes les entités pour le projet Transactional Engine Backend.

## 📋 Vue d'ensemble

Le projet utilise **Liquibase** pour gérer les migrations de base de données. Les migrations sont automatiquement exécutées au démarrage de l'application.

### Entités créées :

- ✅ `jhi_user` (avec les nouveaux champs : telephone, nin, date_naissance, password)
- ✅ `compte` (relation 1-1 avec User)
- ✅ `transfert`
- ✅ `details_transaction` (entité associative)

## 🚀 Option 1 : Développement avec H2 (Recommandé pour commencer)

H2 est une base de données en mémoire/fichier, parfaite pour le développement. **Aucune configuration supplémentaire n'est nécessaire !**

### Étapes :

1. **Démarrer l'application** :

   ```bash
   ./mvnw spring-boot:run
   ```

   Ou avec le profil dev et api-docs :

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,api-docs
   ```

2. **Liquibase s'exécute automatiquement** :

   - Au démarrage, Liquibase détecte les migrations dans `src/main/resources/config/liquibase/`
   - Il crée automatiquement toutes les tables
   - La base de données H2 est créée dans : `./target/h2db/db/transactionalEngineBackend`

3. **Accéder à la console H2** (optionnel) :
   - URL : `http://localhost:9089/h2-console`
   - JDBC URL : `jdbc:h2:file:./target/h2db/db/transactionalEngineBackend`
   - Username : `transactionalEngineBackend`
   - Password : (laisser vide)

### ✅ Vérification

Une fois l'application démarrée, vous devriez voir dans les logs :

```
Liquibase: Update database schema from version X to Y
```

Les tables suivantes doivent être créées :

- `jhi_user`
- `jhi_authority`
- `jhi_user_authority`
- `compte`
- `transfert`
- `details_transaction`

## 🗄️ Option 2 : Production avec MySQL

Pour utiliser MySQL en production ou en développement local :

### Prérequis :

- Docker installé (recommandé)
- OU MySQL installé localement

### Méthode A : Avec Docker (Recommandé)

1. **Démarrer MySQL avec Docker Compose** :

   ```bash
   docker compose -f src/main/docker/mysql.yml up -d
   ```

2. **Vérifier que MySQL est démarré** :

   ```bash
   docker ps
   ```

   Vous devriez voir le conteneur MySQL en cours d'exécution.

3. **Démarrer l'application avec le profil prod** :

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod,api-docs
   ```

4. **Liquibase créera automatiquement** :
   - La base de données `transactionalenginebackend` (si elle n'existe pas)
   - Toutes les tables avec les relations

### Méthode B : MySQL local

1. **Installer MySQL** (si pas déjà fait)

2. **Créer la base de données** :

   ```sql
   CREATE DATABASE transactionalenginebackend CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Configurer les credentials** dans `src/main/resources/config/application-prod.yml` :

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/transactionalenginebackend?useUnicode=true&characterEncoding=utf8&useSSL=false
       username: root
       password: votre_mot_de_passe
   ```

4. **Démarrer l'application** :
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod,api-docs
   ```

## 📊 Structure des tables créées

### Table `jhi_user` (modifiée)

```sql
- id (varchar(100), PK)
- login (varchar(50), unique)
- first_name, last_name
- email (varchar(254), unique)
- telephone (varchar(20), unique) ← NOUVEAU
- nin (varchar(50), unique) ← NOUVEAU
- date_naissance (date) ← NOUVEAU
- password (varchar(255)) ← NOUVEAU
- activated, lang_key, image_url
- created_by, created_date, last_modified_by, last_modified_date
```

### Table `compte`

```sql
- id (bigint, PK, auto-increment)
- solde (decimal(21,2))
- num_compte (varchar(50), unique)
- date_creation (date)
- user_id (varchar(100), FK vers jhi_user, unique)
- created_by, created_date, last_modified_by, last_modified_date
```

### Table `transfert`

```sql
- id (bigint, PK, auto-increment)
- montant (decimal(21,2))
- date (timestamp)
- created_by, created_date, last_modified_by, last_modified_date
```

### Table `details_transaction`

```sql
- id (bigint, PK, auto-increment)
- compte_emetteur_id (bigint, FK vers compte)
- compte_recepteur_id (bigint, FK vers compte)
- transfert_id (bigint, FK vers transfert)
- created_by, created_date, last_modified_by, last_modified_date
```

## 🔍 Vérifier que tout fonctionne

### 1. Vérifier les logs au démarrage

Cherchez dans les logs :

```
Liquibase: Successfully released change log lock
```

### 2. Tester avec Swagger

1. Accédez à : `http://localhost:9089/swagger-ui.html`
2. Testez l'endpoint d'inscription :
   - POST `/api/auth/inscription/etape1` avec un numéro de téléphone
   - Vérifiez que le code OTP est généré (visible dans les logs)

### 3. Vérifier dans la base de données

**Avec H2 Console** :

```sql
SELECT * FROM jhi_user;
SELECT * FROM compte;
SELECT * FROM transfert;
SELECT * FROM details_transaction;
```

**Avec MySQL** :

```bash
mysql -u root -p transactionalenginebackend
```

Puis :

```sql
SHOW TABLES;
DESCRIBE jhi_user;
DESCRIBE compte;
```

## ⚠️ Résolution de problèmes

### Problème : Liquibase ne s'exécute pas

**Solution** : Vérifiez que le profil `dev` ou `prod` est actif :

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,api-docs
```

### Problème : Erreur de connexion MySQL

**Solution** :

1. Vérifiez que MySQL est démarré : `docker ps` ou `mysql --version`
2. Vérifiez les credentials dans `application-prod.yml`
3. Vérifiez que le port 3306 n'est pas utilisé par un autre service

### Problème : Tables déjà existantes

**Solution** : Liquibase détecte automatiquement les changements déjà appliqués. Si vous voulez réinitialiser :

- **H2** : Supprimez le dossier `./target/h2db/`
- **MySQL** : Supprimez et recréez la base de données

### Problème : Contraintes de clés étrangères

**Solution** : Les migrations Liquibase créent automatiquement toutes les contraintes. Si vous avez des erreurs, vérifiez l'ordre des migrations dans `master.xml`.

## 📝 Fichiers de migration

Les migrations sont dans :

```
src/main/resources/config/liquibase/
├── master.xml (fichier principal)
└── changelog/
    ├── 00000000000000_initial_schema.xml (tables JHipster de base)
    ├── 00000000000003_add_user_fields.xml (champs User)
    ├── 00000000000004_create_compte.xml (table Compte)
    ├── 00000000000005_create_transfert.xml (table Transfert)
    └── 00000000000006_create_details_transaction.xml (table DetailsTransaction)
```

## 🎯 Prochaines étapes

Une fois la base de données créée, vous pouvez :

1. ✅ Tester l'inscription via Swagger
2. ✅ Tester la connexion
3. ✅ Vérifier que les comptes sont créés automatiquement
4. ✅ Implémenter les endpoints de transfert d'argent
5. ✅ Implémenter l'endpoint pour voir le solde

## 📚 Ressources

- [Documentation Liquibase](https://docs.liquibase.com/)
- [Documentation JHipster - Database](https://www.jhipster.tech/using-angular/)
- [Documentation H2 Database](https://www.h2database.com/html/main.html)
