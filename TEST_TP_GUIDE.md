# TP Complet : Tests Unitaires JUnit pour CenterManager

## Objectifs pédagogiques

À la fin de ce TP, vous serez capable de :
- ✅ Écrire des tests unitaires avec JUnit 5
- ✅ Utiliser les annotations de test (`@Test`, `@BeforeEach`, `@DisplayName`)
- ✅ Tester des classes métier (Model) sans dépendances externes
- ✅ Tester des classes d'accès aux données (DAO) avec des mocks
- ✅ Appliquer les bonnes pratiques de test (AAA : Arrange, Act, Assert)
- ✅ Utiliser Mockito pour les tests d'intégration base de données

---

## 📚 Concepts fondamentaux

### Structure d'un test JUnit 5

```java
@Test
@DisplayName("Description claire du test")
void testMethodeName() {
    // Arrange : Préparer les données
    String expectedValue = "test";
    
    // Act : Exécuter l'action à tester
    String actualValue = someMethod();
    
    // Assert : Vérifier le résultat
    assertEquals(expectedValue, actualValue);
}
```

### Cycle de vie d'un test

```
@BeforeEach      ↓
Arrange    → Act    → Assert    → @AfterEach
(Setup)   (Action) (Vérific.)  (Nettoyage)
```

---

## 📋 Structure du projet

```
CenterManager/
├── src/
│   ├── main/java/fr/dawan/CenterManager/
│   │   ├── model/          (Métier - à tester)
│   │   │   ├── Fog.java
│   │   │   ├── Trainer.java
│   │   │   ├── Training.java
│   │   │   ├── RemoteDay.java
│   │   │   └── ...
│   │   ├── dao/            (Persistance - difficile à tester)
│   │   │   ├── Database.java
│   │   │   ├── FogDao.java
│   │   │   ├── TrainerDao.java
│   │   │   └── ...
│   │   └── ...
│   └── test/java/fr/dawan/CenterManager/
│       ├── model/          (Tests du métier)
│       │   ├── FogTest.java
│       │   ├── TrainerTest.java
│       │   ├── TrainingTest.java
│       │   └── RemoteDayTest.java
│       └── dao/            (Tests de persistance)
│           ├── FogDaoTest.java
│           ├── TrainerDaoTest.java
│           ├── TrainingDaoTest.java
│           └── TrainerRdDaoTest.java
└── pom.xml
```

---

## 🚀 Démarrage

### 1. Ajouter les dépendances (DÉJÀ FAIT)

Le `pom.xml` contient maintenant :
```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.2.0</version>
    <scope>test</scope>
</dependency>
```

### 2. Exécuter les tests

```bash
# Exécuter tous les tests
mvn test

# Exécuter les tests d'une classe spécifique
mvn test -Dtest=FogTest

# Exécuter un test spécifique
mvn test -Dtest=FogTest#testConstructorSimple
```

---

## 📖 Tutoriel progressif

### Niveau 1 : Tests basiques du Model (Fog)

**Fichier :** [FogTest.java](../src/test/java/fr/dawan/CenterManager/model/FogTest.java)

#### Exemple 1 : Test d'un constructeur

```java
@Test
@DisplayName("Créer un Fog avec constructeur simple")
void testConstructorSimple() {
    // Arrange & Act
    Fog newFog = new Fog("JavaScript");

    // Assert
    assertEquals("JavaScript", newFog.getName());
    assertNull(newFog.getDescription());
}
```

**Explications :**
- `@Test` : Marque cette méthode comme un test
- `@DisplayName()` : Nom lisible du test
- `assertEquals()` : Vérifie que deux valeurs sont égales
- `assertNull()` : Vérifie qu'une valeur est null

#### Exemple 2 : Test avec état initial

```java
@BeforeEach
void setUp() {
    // Cette méthode s'exécute AVANT chaque test
    fog = new Fog("Java Avancé", "Formation complète en Java");
    fog.setId(1);
    fog.setTrainingId(10);
}

@Test
@DisplayName("Modifier le nom d'un Fog")
void testSetName() {
    // Arrange
    String newName = "Java Débutant";

    // Act
    fog.setName(newName);

    // Assert
    assertEquals(newName, fog.getName());
}
```

**Explications :**
- `@BeforeEach` : Initialise l'état avant chaque test (permet de partager du code)
- Les trois phases (AAA) sont bien séparées avec des commentaires

---

### Niveau 2 : Tests d'objets complexes (Trainer)

**Fichier :** [TrainerTest.java](../src/test/java/fr/dawan/CenterManager/model/TrainerTest.java)

#### Exemple : Test avec collections

```java
@BeforeEach
void setUp() {
    // Initialiser une liste de RemoteDay
    remoteDays = new ArrayList<>();
    remoteDays.add(new RemoteDay(1, 1, "Lundi"));
    remoteDays.add(new RemoteDay(2, 1, "Mercredi"));

    trainer = new Trainer(1, "Jean", "Dupont", remoteDays);
}

@Test
@DisplayName("Vérifier qu'un formateur a les bons jours distants")
void testTrainerWithRemoteDays() {
    // Assert
    assertEquals(2, trainer.getRemoteDays().size());
    assertTrue(trainer.getRemoteDays().stream()
            .anyMatch(rd -> rd.getDay().equals("Lundi")));
}
```

**Points clés :**
- Utiliser `stream()` pour filtrer et vérifier des collections
- `assertTrue()` pour vérifier une condition booléenne
- `assertEquals()` pour comparer la taille des collections

---

### Niveau 3 : Tests des interfaces (Displayable)

**Exemple :** Tester l'implémentation d'une interface

```java
@Test
@DisplayName("Récupérer les champs d'affichage")
void testGetDisplayFields() {
    // Act
    Map<String, Object> fields = fog.getDisplayFields();

    // Assert
    assertNotNull(fields);                    // Pas null
    assertTrue(fields.containsKey("Nom"));     // Contient la clé
    assertEquals("Java Avancé", fields.get("Nom")); // Bonne valeur
}
```

**Assertions utiles :**
- `assertNotNull()` : Vérifie que l'objet n'est pas null
- `assertNull()` : Vérifie que l'objet est null
- `assertTrue()` : Vérifie une condition vraie
- `assertFalse()` : Vérifie une condition fausse
- `assertThrows()` : Vérifie qu'une exception est levée

---

### Niveau 4 : Tests du DAO (difficile)

**Fichier :** [FogDaoTest.java](../src/test/java/fr/dawan/CenterManager/dao/FogDaoTest.java)

#### Problème : La base de données est difficile à tester

```java
// ❌ MAUVAIS : Dépend de la vraie base de données
@Test
void testInsertFog() throws SQLException {
    // Cela va échouer si la BD est pas disponible !
    Fog fog = new Fog("Test");
    Fog inserted = fogDao.insert(fog);
    // ...
}
```

#### Solution 1 : Test sans base de données (ce qui est fait)

```java
@Test
@DisplayName("Créer un Fog depuis un nom")
void testCreateFromName() throws SQLException {
    // Arrange
    String name = "Java Avancé";

    // Act
    Fog fog = fogDao.createFromName(name);

    // Assert
    assertNotNull(fog);
    assertEquals(name, fog.getName());
}
```

**Avantage :** ✅ Rapide, ✅ Fiable, ✅ Pas de BD requise

---

## 💡 Bonnes pratiques

### 1. Nommer vos tests clairement

```java
// ✅ BON : Clair et lisible
void testCreateFogWithNameAndDescription() { }

// ❌ MAUVAIS : Trop court, pas clair
void test1() { }

// 🎯 MEILLEUR : Avec @DisplayName
@DisplayName("Créer un Fog avec nom et description")
void testCreateFogWithNameAndDescription() { }
```

### 2. Utiliser le pattern AAA

```java
void testMethod() {
    // Arrange : Préparer
    Fog fog = new Fog("Test");
    
    // Act : Exécuter
    fog.setName("Nouveau");
    
    // Assert : Vérifier
    assertEquals("Nouveau", fog.getName());
}
```

### 3. Un test = un cas de test

```java
// ❌ MAUVAIS : Teste plusieurs choses
void testFog() {
    fog.setName("Test");
    assertEquals("Test", fog.getName());
    fog.setDescription("Desc");
    assertEquals("Desc", fog.getDescription());
    // ...
}

// ✅ BON : Chaque test teste une chose
void testSetNameChangesName() { /* ... */ }
void testSetDescriptionChangesDescription() { /* ... */ }
```

---

## 🎓 Exercices progressifs

### Exercice 1 : Écrire un premier test (Facile - 5 min)

**Créer un test pour la classe `RemoteDay` :**

```java
@Test
@DisplayName("Créer un RemoteDay avec tous les paramètres")
void testRemoteDayConstructor() {
    // TODO : Utiliser le constructeur RemoteDay(1, 5, "Lundi")
    // TODO : Vérifier que id = 1, trainerId = 5, day = "Lundi"
}
```

**Solution :** Voir [RemoteDayTest.java](../src/test/java/fr/dawan/CenterManager/model/RemoteDayTest.java)

---

### Exercice 2 : Test avec collections (Moyen - 10 min)

**Créer un test pour vérifier la liste des formateurs :**

```java
@Test
@DisplayName("Vérifier qu'une formation a au moins un FOG"""
void testTrainingHasRequiredFogs() {
    // TODO : Créer une Training avec au moins 2 FOGs
    // TODO : Vérifier que la taille de getRequiredFogs() est 2
    // TODO : Vérifier qu'un FOG spécifique existe
}
```

**Indices :**
- Utiliser `List.of()` ou `new ArrayList<>()`
- Utiliser `.stream().anyMatch()` pour chercher un élément

---

### Exercice 3 : Test d'une interface (Moyen - 15 min)

**Créer un test pour vérifier le contrat de l'interface `Displayable` :**

```java
@Test
@DisplayName("Vérifier que Fog implémente correctement Displayable")
void testFogImplementsDisplayable() {
    // TODO : Créer un Fog
    // TODO : Vérifier que getDisplayName() retourne le nom correct
    // TODO : Vérifier que getDisplayFields() retourne une Map non-null
    // TODO : Vérifier qu'une clé attendue existe dans la Map
}
```

---

### Exercice 4 : Test avec exception (Difficile - 20 min)

**Créer un test pour vérifier qu'une exception est levée :**

```java
@Test
@DisplayName("Vérifier que setName(...) accepte null")
void testSetNameWithNull() {
    // TODO : Appeler fog.setName(null)
    // TODO : Vérifier le comportement (exception ou null accepté)
    // Utiliser assertThrows() ou assertDoesNotThrow()
}
```

---

## 🧪 Checklist de test

Pour chaque classe à tester, vérifiez :

- [ ] **Constructeurs** : Tous les constructeurs sont testés
- [ ] **Getters** : Tous les getters retournent la bonne valeur
- [ ] **Setters** : Tous les setters modifient la bonne valeur
- [ ] **États limites** : Tester null, vide, négatif, zéro, etc.
- [ ] **Collections** : Tailles, éléments, filtrage
- [ ] **Exceptions** : Les bonnes exceptions sont levées
- [ ] **Interfaces** : Le contrat est respecté

---

## 📊 Mesurer la couverture de test

```bash
# Générer un rapport de couverture (avec JaCoCo)
mvn clean test jacoco:report
```

Le rapport est généré dans `target/site/jacoco/index.html`

**Objectifs de couverture :**
- 🎯 80%+ pour le code métier (Model)
- 🎯 60%+ pour le code d'accès (DAO) avec mocks

---

## 🔗 Liens utiles

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Assertions JUnit](https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html)

---

## 📝 Résumé des tests fournis

| Classe | Tests | Linea |
|--------|-------|-------|
| `Fog` | 10 tests | [FogTest.java](../src/test/java/fr/dawan/CenterManager/model/FogTest.java) |
| `Trainer` | 11 tests | [TrainerTest.java](../src/test/java/fr/dawan/CenterManager/model/TrainerTest.java) |
| `Training` | 12 tests | [TrainingTest.java](../src/test/java/fr/dawan/CenterManager/model/TrainingTest.java) |
| `RemoteDay` | 8 tests | [RemoteDayTest.java](../src/test/java/fr/dawan/CenterManager/model/RemoteDayTest.java) |
| `FogDao` | 3 tests | [FogDaoTest.java](../src/test/java/fr/dawan/CenterManager/dao/FogDaoTest.java) |
| `TrainerDao` | 5 tests | [TrainerDaoTest.java](../src/test/java/fr/dawan/CenterManager/dao/TrainerDaoTest.java) |
| `TrainingDao` | 6 tests | [TrainingDaoTest.java](../src/test/java/fr/dawan/CenterManager/dao/TrainingDaoTest.java) |
| `TrainerRdDao` | 7 tests | [TrainerRdDaoTest.java](../src/test/java/fr/dawan/CenterManager/dao/TrainerRdDaoTest.java) |
| **TOTAL** | **62 tests** | |

---

## 🎯 Prochaines étapes

1. ✅ Exécuter les tests : `mvn test`
2. ✅ Analyser les résultats
3. ✅ Ajouter d'autres tests pour les cas limites
4. ✅ Utiliser Mockito pour tester avec une vraie BD
5. ✅ Intégrer les tests dans le CI/CD (GitHub Actions, GitLab CI)

---

**Bon courage ! 🚀**
