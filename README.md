# Système Multi-Agents : Marketplace JADE

Ce dépôt contient le code source d'un projet de conception et de développement d'un Système Multi-Agents (SMA) basé sur le middleware **JADE (Java Agent DEvelopment Framework)**. 

Ce projet a été réalisé dans le cadre du module **IA distribuée & Systèmes multi-agents** à l'**ENSIAS** et simule un environnement transactionnel (Marketplace) de complexité croissante.

## 📋 Table des matières
1. [Aperçu du projet](#-aperçu-du-projet)
2. [Fonctionnalités implémentées](#-fonctionnalités-implémentées)
3. [Technologies et Prérequis](#-technologies-et-prérequis)
4. [Installation et Exécution](#-installation-et-exécution)
5. [Auteurs](#-auteurs)

---

##  Aperçu du projet

L'objectif de cette application est d'explorer les concepts fondamentaux de l'informatique distribuée orientée agent. Le projet est structuré en 4 grandes étapes (Travaux Pratiques) :
- Initialisation et cycle de vie d'un agent.
- Modélisation de comportements complexes.
- Découverte dynamique de services et négociation avancée.
- Mobilité du code à travers le réseau.

---

##  Fonctionnalités implémentées

### 1. Prise en main et Cycle de vie (TP1)
- Lancement du **Main-Container** et de conteneurs secondaires.
- Déploiement d'agents et gestion de leur cycle de vie (`setup`, `beforeMove`, `afterMove`, `takeDown`).
- Mobilité basique inter-conteneurs déclenchée via l'interface graphique (RMA).

### 2. Comportements & Communication FIPA-ACL (TP2)
- Architecture **Producteur / Consommateur**.
- Communication asynchrone normée par les performatives FIPA-ACL (`REQUEST`, `PROPOSE`, `ACCEPT_PROPOSAL`).
- Utilisation des `CyclicBehaviour` et `WakerBehaviour` pour modéliser des comportements réactifs et proactifs.
- Analyse des échanges réseaux via l'outil **Sniffer**.

### 3. Service d'Annuaire (DF) et FIPA Contract Net (TP3)
- Inscription et recherche de services dans les Pages Jaunes de JADE (**Directory Facilitator - DF**).
- Implémentation complète du protocole d'enchères **FIPA Contract Net** (`CFP`, `PROPOSE`, `REFUSE`, `ACCEPT`, `INFORM`).
- Prise de décision autonome de l'acheteur pour sélectionner le vendeur offrant le meilleur prix.

### 4. Agents Mobiles (TP4)
- **Itinéraire Statique :** Migration d'un agent (`MobileAgent`) sur une liste prédéfinie de conteneurs pour y déposer une charge utile (`StationaryAgent`).
- **Itinéraire Dynamique :** Communication ontologique de l'agent avec l'**Agent Management System (AMS)** via la `QueryPlatformLocationsAction` pour découvrir les conteneurs actifs de la plateforme et construire son itinéraire de voyage de manière 100% autonome.

---

## Technologies et Prérequis

- **Langage :** Java (JDK 8 ou supérieur)
- **Framework :** [JADE 4.6.0](http://jade.tilab.com/) (`jade.jar`)
- **IDE :** Visual Studio Code / Eclipse

---

## Installation et Exécution

1. **Cloner le dépôt :**
   ```bash
   git clone [https://github.com/OuidadELBJ/SMA-Marketplace-JADE.git](https://github.com/OuidadELBJ/SMA-Marketplace-JADE.git)
   cd SMA-Marketplace-JADE
