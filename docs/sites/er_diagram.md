# ER-diagram

[<kbd>&larr; Go Back</kbd>](../../README.md)

[<kbd>**View how to create an ER-diagram** &rarr;</kbd>](how_to_create_er_diagram.md)

<img src="../pictures/current_er.png">

## Warum answers eine n-to-n Beziehung zu questions hat

Bei der Umsetzung haben wir zwei Möglichkeiten:
1. eine 1-to-n Beziehung machen und bei answers den foreign-key von questions hinzufügen
2. oder eine n-to-n Beziehung machen und bei questions mit keinen Antworten auf "" in answers verweisen

Wir nehmen die zweite Möglichkeit, damit wir referenzielle Integrität in der Datenbank haben 
und Werte nicht doppelt abgespeichert werden, auch wenn es in diesem Fall nur um "" geht.

## Normalisierung nach den 3 Normalformen
### 1. Normalform
*Nur elementare Merkmalswerte sind erlaubt*

Werte der Atrribute sind atomar (d.h keine zusammengesetzten Werte oder Mehrfachwerte)

### 2. Normalform
*abhängig von allen Schlüsselmerkmalen*

1.NF + Nichtschlüsselmerkmale sind von allen Schlüsselmerkmalen voll funktional abhängig.

### 3. Normalform
*nicht abhängig über Umwege*

3.NF = 2.NF + kein Nichtschlüsselmerkmal ist von irgendeinem Schlüssel transitiv 
(also indirekt, über Umwege) abhängig