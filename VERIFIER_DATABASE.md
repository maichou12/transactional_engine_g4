# 🔍 Vérifier que la base de données est en marche

## ✅ Méthode 1 : Vérifier les logs de Liquibase

Dans les logs de votre application, cherchez ces messages qui indiquent que Liquibase a bien exécuté les migrations :

```
Liquibase: Successfully released change log lock
```

ou

```
Liquibase: Update database schema from version X to Y
```

ou

```
Liquibase: Successfully ran X change(s)
```

**Si vous ne voyez pas ces messages**, Liquibase n'a peut-être pas encore fini ou il y a un problème.

---

## 🗄️ Méthode 2 : Vérifier directement dans MySQL

Puisque vous avez le conteneur MySQL qui tourne (`transactionalenginebackend-mysql-1`), connectez-vous :

### Option A : Via Docker (Recommandé)

```bash
# Se connecter au conteneur MySQL
docker exec -it transactionalenginebackend-mysql-1 mysql -u root transactionalenginebackend

# Puis exécutez ces commandes SQL :
SHOW TABLES;

# Vous devriez voir :
# - jhi_user
# - jhi_authority
# - jhi_user_authority
# - compte
# - transfert
# - details_transaction
# - databasechangelog (table Liquibase)
# - databasechangeloglock (table Liquibase)

# Vérifier la structure d'une table
DESCRIBE jhi_user;
DESCRIBE compte;
DESCRIBE transfert;
DESCRIBE details_transaction;

# Vérifier les colonnes ajoutées à jhi_user
SHOW COLUMNS FROM jhi_user LIKE 'telephone';
SHOW COLUMNS FROM jhi_user LIKE 'nin';
SHOW COLUMNS FROM jhi_user LIKE 'date_naissance';
SHOW COLUMNS FROM jhi_user LIKE 'password';
```

### Option B : Via client MySQL local

```bash
mysql -h 127.0.0.1 -P 3306 -u root transactionalenginebackend
```

Puis les mêmes commandes SQL ci-dessus.

---

## 🧪 Méthode 3 : Tester avec l'API

Testez l'endpoint d'inscription pour vérifier que tout fonctionne :

1. **Accédez à Swagger** : `http://localhost:9089/swagger-ui.html`

2. **Testez l'inscription** :

   - POST `/api/auth/inscription/etape1`
   - Body : `{"telephone": "+221771234567"}`
   - Si ça fonctionne, la base de données est OK !

3. **Vérifiez dans les logs** :
   - Vous devriez voir : `Code OTP généré pour le téléphone ...`

---

## 🔍 Méthode 4 : Vérifier via l'endpoint de santé

```bash
curl http://localhost:9089/management/health
```

Vous devriez voir :

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

---

## ⚠️ Si les tables n'existent pas

### Solution 1 : Forcer l'exécution de Liquibase

Si Liquibase n'a pas encore exécuté les migrations, vous pouvez :

1. **Vérifier que le profil dev est actif** dans les logs :

   ```
   Startup profile(s) dev
   ```

2. **Redémarrer l'application** :
   ```bash
   # Arrêtez l'application (Ctrl+C)
   # Puis redémarrez
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,api-docs
   ```

### Solution 2 : Vérifier la configuration

Vérifiez dans `src/main/resources/config/application-dev.yml` que :

```yaml
spring:
  liquibase:
    contexts: dev, faker
```

### Solution 3 : Exécuter Liquibase manuellement

```bash
# Avec Maven
./mvnw liquibase:update -Dspring.profiles.active=dev
```

---

## 📊 Commandes SQL utiles pour vérifier

### Compter les tables

```sql
SELECT COUNT(*) as nombre_tables
FROM information_schema.tables
WHERE table_schema = 'transactionalenginebackend';
-- Devrait retourner au moins 8 tables
```

### Vérifier les migrations Liquibase appliquées

```sql
SELECT id, author, filename, dateexecuted
FROM databasechangelog
ORDER BY dateexecuted DESC;
-- Vous devriez voir toutes vos migrations listées
```

### Vérifier les contraintes de clés étrangères

```sql
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'transactionalenginebackend'
  AND REFERENCED_TABLE_NAME IS NOT NULL;
-- Devrait montrer les relations entre les tables
```

---

## ✅ Checklist de vérification

- [ ] Le conteneur MySQL est en cours d'exécution (`docker ps`)
- [ ] Les logs montrent "Liquibase: Successfully released change log lock"
- [ ] La commande `SHOW TABLES;` retourne au moins 8 tables
- [ ] La table `jhi_user` a les colonnes `telephone`, `nin`, `date_naissance`, `password`
- [ ] La table `compte` existe avec une clé étrangère vers `jhi_user`
- [ ] La table `transfert` existe
- [ ] La table `details_transaction` existe avec les clés étrangères
- [ ] L'endpoint `/api/auth/inscription/etape1` fonctionne dans Swagger

---

## 🚨 Problèmes courants

### Problème : "Table doesn't exist"

**Cause** : Liquibase n'a pas encore exécuté les migrations

**Solution** :

1. Attendez quelques secondes (Liquibase peut être asynchrone)
2. Vérifiez les logs pour des erreurs Liquibase
3. Redémarrez l'application

### Problème : "Connection refused" à MySQL

**Cause** : Le conteneur MySQL n'est pas démarré ou le port est incorrect

**Solution** :

```bash
# Vérifier que MySQL tourne
docker ps | grep mysql

# Si pas de résultat, démarrer MySQL
docker compose -f src/main/docker/mysql.yml up -d
```

### Problème : Tables existent mais pas les nouvelles colonnes

**Cause** : La migration `00000000000003_add_user_fields.xml` n'a pas été exécutée

**Solution** :

```sql
-- Vérifier si la migration a été appliquée
SELECT * FROM databasechangelog WHERE id = '00000000000003';

-- Si pas de résultat, forcer l'exécution
```

---

## 📝 Note importante

Si vous utilisez le profil **dev**, l'application utilise **H2** (pas MySQL) par défaut !

- H2 : Base de données fichier dans `./target/h2db/db/transactionalEngineBackend`
- MySQL : Utilisé avec le profil **prod**

Pour utiliser MySQL en développement, changez le profil ou modifiez `application-dev.yml`.
