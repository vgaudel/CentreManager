// package fr.dawan.CenterManager.controller;

// import fr.dawan.CenterManager.analysis.PlanAnalyzer;
// import fr.dawan.CenterManager.model.PlanAnalysisResult;

// public class PlanController {

//     public void runTask() {
//         // Exemple simple dans une méthode de PlanController
//         PlanAnalyzer analyzer = new PlanAnalyzer();
//         PlanAnalysisTask task = new PlanAnalysisTask(image, analyzer);

//         // Bind de l'UI
//         progressBar.progressProperty().bind(task.progressProperty());
//         statusLabel.textProperty().bind(task.messageProperty());

//         // Lancer la task
//         new Thread(task).start();

//         // Quand c'est terminé
//         task.setOnSucceeded(event -> {
//             PlanAnalysisResult result = task.getValue();
//             System.out.println("Analyse terminée, zones trouvées : " + result.getZones().size());
//         });

//     }

// }
