# AIZUL - Ekspertski sistem za igranje društvene igre Azul

Članovi tima:
- Boris Markov SV73/2021
- Gojko Vučković SV49/2021

# Opis problema

### Motivacija

Tokom poslednje decenije industrija društvenih igara je postala mnogo razvijenija i pristupačnija prosečnom čoveku. Razvijaju se nove i moderne društve igre različitih kompleksnosti koje vrlo brzo postaju "kultni klasici" na policama kolekcionara, porodica, pa i povremenih igrača. Jedna od ovih igara, koju bismo želeli da istaknemo je upravo ***Azul*** iz 2017. godine. Smatramo da ova igra ima veliku kompleksnost i različite zanimljive strategije igranja, ali sa vrlo jednostavnim pravilima igranja. Detaljnije o igri možete pročitati [ovde](https://en.wikipedia.org/wiki/Azul_(board_game)).

Obojica volimo ovu igru i često je igramo, sa time u vidu, želimo da odamo počast ***Azul***-u i približimo ljudima time što ćemo napraviti pametnog agenta, uz kojeg će ljudi moći da interaktivno nauče da je igraju, ili unapređuju svoje veštine.

- - -
### Pregled problema

Ukratko, pravila igre su sledeća:

- Igrači skupljaju pločice (keramičke pločice različitih boja i dezena) i postavljaju ih na svoju ličnu tablu sa ciljem da ukrase zid palate. U svakoj rundi postoje krugovi („fabrike“) iz kojih se biraju pločice. Kada je igrač na potezu, on izabere sve pločice iste boje iz jedne fabrike i prenese ih na jednu od svojih redova za pripremu. Sve ostale pločice iz te fabrike premeštaju se u centralni krug i odatle ih kasnije igrači mogu uzimati.

- Kada se svi krugovi isprazne, završava se runda i pločice iz popunjenih redova na tabli se premeštaju na odgovarajuće mesto na „zid“. Svaka postavljena pločica donosi poene u zavisnosti od toga koliko se dobro uklapa sa već postavljenim pločicama (horizontalno i vertikalno povezivanje). Ploče koje ne mogu da se smeste idu na „kaznenu liniju“ i oduzimaju poene.

- Igra traje dok neki igrač ne popuni jedan horizontalni red na svom zidu. Nakon toga sledi završno bodovanje – dodatni poeni se dobijaju za kompletne redove, kolone ili svih pet pločica iste boje. Pobednik je igrač sa najviše osvojenih poena.

![Prikaz jednog stanja igre](slika1.png)
*Slika 1 - Prikaz jednog stanja table igrača*

Nalik šahu, pravila igre su prilično jednostavna, međutim postoji bezbroj različitih strategija za igranje i odabir najboljeg poteza može biti vrlo kompleksan. Sa time, želimo da napravimo agenta sa osnovnom funkcionalnošću da odredi najbolji mogući potez u datom stanju table.

Većina implementacija ekspertskih sistema za igranje tabličnih igara poput ***Azul*** su zasnovana na algoritmima poput ***minimax***, ***Monte Carlo Tree Search*** ili ***mašinskog učenja***. Interaktivni sistemi koji su namenjeni da objasne i nauče igrače da igraju se često zadržavaju samo na jednoj rundi (ili par njih) koja je skoro uvek unapred predodređena i linearna bez prilike za učenje kompleksnih strategija. Sa time, naš projekat bi zasnovali na *pravilima*, *forward i backwards chaining*, i *complex event processing* (za šta smatramo da je ***Azul*** naročito pogodan), sa funkcionalnošću da **objasni odluku svog poteza koji smatra optimalnim**. Verujemo da će ovakav pristup omogućiti korisnicima da bolje nauče da igraju ***Azul*** od drugih već postojećih rešenja.

Primeri implementacija, inspiracije i relevantna literatura:
1. [AlphaGo - program koji igra tabličnu igru Go](https://deepmind.google/research/projects/alphago/)
2. [AzulAI - *"program koji igra Azul bolje od čoveka"*](https://domwil.co.uk/posts/azul-ai/#comparison)
3. [The Design of Azul - analiza i tekst o tome šta je dobar izbor u igrama](https://jboger.medium.com/the-design-of-azul-8ab36d21d3a5)

- - -
### Metodologija rada

#### Primena forward i backward chaining-a

*Forward chaining* kreće od trenutnog stanja igre i primenjuje pravila dok ne pronađe validne akcije. Na primer:
- Ako u fabrici postoje 3 pločice boje X, a naš red prima tačno 3 pločice → predloži da se one uzmu.
- Ako u centru postoji kombinacija pločica koja vodi u kazneni red → dodeli tom potezu niži prioritet.

*Backward chaining* polazi od cilja, npr. „maksimizacija poena u sledećoj rundi“, i proverava da li postoje uslovi da se on postigne. Na primer: „Da li neki od poteza vodi do završavanja reda/kolone?“ Ako da, potez se potvrđuje kao optimalan. Ili, ako je cilj „sprečiti protivnika da završi red“, sistem proverava da li uzimanjem određene boje može da blokira protivnika.

Kombinovanjem ova dva pristupa agent prvo pomoću forward chaining-a generiše skup svih mogućih poteza, a zatim kroz backward chaining proverava koji od njih zadovoljavaju viši cilj (maksimizacija bodova, dugoročna strategija, blokiranje protivnika). Na taj način sistem uspeva da balansira kratkoročne koristi sa dugoročnim planiranjem.

#### Primena Complex Event Processing-a (CEP)
*CEP* bi u našem sistemu imao svrhu da prepozna obrasce događaja kroz više poteza i reaguje na njih. Na primer:
- Ako u toku iste runde više igrača ciljaju istu boju, sistem detektuje „konflikt oko resursa“ i može da predloži da naš agent uzme sve te pločice, kako bi sebi obezbedio prednost i istovremeno blokirao protivnika.
- Ako se tokom više rundi ponavlja obrazac da agent ostavlja nepovoljnu količinu pločica u centru (npr. često „gura“ višak pločica protivnicima), sistem može da prepozna taj trend i promeni heuristiku, tako da agent ubuduće izbegava poteze koji daju protivnicima očiglednu prednost.

Na ovaj način CEP omogućava da agent ne reaguje samo na trenutno stanje, već i na tok igre i interakcije među igračima.

#### Primeri pravila

- Pravilo 1: Ako u fabrici postoje pločice boje X i u našem redu nedostaje tačno toliko pločica → potez dobija visok prioritet.
- Pravilo 2: Ako uzimanje pločica boje X vodi do popunjavanja reda poda → potez dobija nizak prioritet.
- Pravilo 3: Ako uzimanjem pločica boje X protivnik ne može da završi svoj red → potez dobija dodatnu vrednost.
- Pravilo 4: Ako postoji izbor između poteza koji završava red i poteza koji započinje novu kolonu, a oba daju isti broj bodova → prioritet ima potez koji vodi ka formiranju bonus poena (kolona, svih pet boja).

#### Ulaz programa
- Stanje table svakog od igrača
	- Zid
	- Šema za pločice
	- Red poda
- Stanje fabrika i centra stola
- Broj igrača
#### Izlaz programa
- Predlog poteza
- Rezonovanje izbora
#### Baza znanja
- Pravila igre
- Heuristike za donošenje odluka
- Šabloni tipičnih situacija i primećena ponašanja igrača
  
- - -
### Rezonovanje konkretnog primera
1. Sistem dobija stanje: u fabrici A ima 3 plave pločice, u fabrici B 2 crvene, u centru 1 žuta i 2 crne.
2. Znanje: igrač trenutno ima nedovršen red od 3 plave pločice, pa mu fali još 2 da završi, takođe do sada igrač nema ni jednu pločicu u redu poda.
3. Pravilo: ako postoji mogućnost da se završi red → potez ima visoku prioritetnu vrednost.
4. Forward chaining: sistem identifikuje da uzimanje 3 plave pločice iz fabrike A omogućava završetak reda.
5. Backward chaining: sistem proverava cilj „maksimizacija bodova u ovom krugu“ i zaključuje da je potez konzistentan sa tim ciljem.
6. Output: „Uzmi 3 plave pločice iz fabrike A, i stavi u red 5, zato što ćeš završiti taj red, iako će ti 1 pločica oduzeti 1 bod.“.
