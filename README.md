# PSICQUICHelper

Console app to download, cluster and score protein interactions in MITab format.

## Download
Download interactions form PSICQUIC servers.

### Arguments
```
-q,--query <query>     PSICQUIC query to use
-services <services>   list of services to query
-o,--out <o>           output folder
```

### Examples

Download human interactions from all servers
```
java -jar PSICQUICHelper.jar -dwl -q species:human -o data
```

Download human interactions from IntAct and MINT
```
java -jar PSICQUICHelper.jar -dwl -q species:human -o data -s IntAct,MINT
```

Download all interactions from all servers
```
java -jar PSICQUICHelper.jar -dwl -o data
```

## Cluster
```
Cluster and score a MITab file.
```

### Arguments
```
-sn <sn>               score name
-mappings <mappings>   priority for molecule accession mapping
```

### Examples

Cluster and score file interactions.tab
```
java -jar PSICQUICHelper.jar -cl interactions.tab
```

Cluster and score file interactions.tab. Score name set to myscore
```
java -jar PSICQUICHelper.jar -cl interactions.tab -sn myscore
```

## Map
Map identifiers/interactions from file to UniProt.

### Arguments
```
-o,--out <o>           mapping index folder
-i, --i <i>            set in order to map interactions
```

### Examples

Map file identifiers.tab
```
java -jar PSICQUICHelper.jar -map identifiers.tab -o map_folder 
```

Map file interactions.tab
```
java -jar PSICQUICHelper.jar -map identifiers.tab -o map_folder -i
```

## Merge
Merge mitab files

### Examples

Merge tab files in folder data
```
java -jar PSICQUICHelper.jar -m data
```