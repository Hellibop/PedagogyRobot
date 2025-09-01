# Varför?

Målet med en gemensam kodstandard är följande:

- Öka lättläsligheten och minska komplexiteten  
- Lättare att hitta errors  
- Uppmuntrar till bra programmeringspraxis  

## Generella bestämmelser

### Formatering

Bra indentering är viktig för att underlätta hur läsbar koden är. Ingen gillar att läsa en vägg av text. Följ följande riktlinjer:

- Whitespace efter komma mellan två parametrar  
- Varje nested block ska vara korrekt indenterat, tänk Python  
- Braces `{}` efter funktioner och if-satser ska starta på samma rad som funktionsnamnet och sluta en rad under sista  
- Fyra spaces som indentering  
- Inte rader längre än 100 tecken  
- Använd tomma rader för att öka läsbarheten  

Använd guard clauses: [Guard Clauses](https://medium.com/lemon-code/guard-clauses-3bc0cd96a2d3)  

### Namngivning

- **Tydliga namn**: Undvik att namnge variabler med enstaka bokstäver eller förkortningar för att göra det lättare för andra att förstå.  
- **Inga siffror**: Undvik siffror i variabelnamn  
- **Funktionsnamn**: Namnet på funktionen ska beskriva vad den gör.  

### Kommentarer

- Kommentar som beskriver varje funktion. Ska finnas innan varje funktion.  

### Organisation

- **Begränsat antal globala variabler**: Gör det mycket lättare att förstå vad som sker i koden och varför.  
- **Filnamn**: Om en fil enbart innehåller ett objekt borde objektet och filens namn matcha.  

## Språkspecifika standarder

### Kotlin  
[Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)  

- **Klasser**: `MyClass`  
- **Funktioner**: `myFunction`  
- **Variabler**: `myVariable`  
- **Konstanter**: `MAX_COUNT`  

### React  

### Express.js / JavaScript  
