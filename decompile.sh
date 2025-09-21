#!/bin/bash

# Find the obr or jar in the current directory (exclude decompiler jars)
plugin_bundle=$(find . -maxdepth 1 \( -name "*.jar" -o -name "*.obr" \) ! -name "cfr-*.jar" ! -name "procyon-*.jar" | head -1)

if [ -z "$plugin_bundle" ]; then
    echo "No .jar or .obr file found in the current directory"
    exit 1
fi

echo "Found bundle: $plugin_bundle"

# Create directories for decompiled output
mkdir -p decompiled/cfr
mkdir -p decompiled/procyon

# Paths to decompilers are required via environment variables
: "${CFR_PATH:?Environment variable CFR_PATH must point to cfr-<version>.jar}"
: "${PROCYON_PATH:?Environment variable PROCYON_PATH must point to procyon-decompiler-<version>.jar}"

# Check if decompilers exist
if [ ! -f "$CFR_PATH" ]; then
    echo "CFR decompiler not found at $CFR_PATH"
    exit 1
fi

if [ ! -f "$PROCYON_PATH" ]; then
    echo "Procyon decompiler not found at $PROCYON_PATH"
    exit 1
fi

# Extract if it's an OBR file (OBR files are ZIP archives)
if [[ "$plugin_bundle" == *.obr ]]; then
    echo "Extracting OBR file..."
    mkdir -p obr-extracted
    unzip -q "$plugin_bundle" -d obr-extracted
    
    # Find all JAR files inside the extracted content (exclude decompiler jars)
    jar_files=$(find obr-extracted -name "*.jar" ! -name "cfr-*.jar" ! -name "procyon-*.jar")
    
    if [ -z "$jar_files" ]; then
        # If no JARs found, look for class files directly
        echo "No JAR files found in OBR, looking for class files..."
        class_files=$(find obr-extracted -name "*.class")
        
        if [ -n "$class_files" ]; then
            echo "Decompiling extracted class files..."
            
            # Decompile using CFR
            echo "Decompiling with CFR..."
            java -jar "$CFR_PATH" obr-extracted --outputdir decompiled/cfr
            
            # Decompile using Procyon
            echo "Decompiling with Procyon..."
            java -jar "$PROCYON_PATH" obr-extracted -o decompiled/procyon
        else
            echo "No class files found in the OBR archive."
            exit 1
        fi
    else
        echo "Found JAR files in OBR:"
        echo "$jar_files"
        
        # Create directory for each JAR file
        for jar_file in $jar_files; do
            # Get basename of jar file without extension for directory naming
            jar_basename=$(basename "$jar_file" .jar)
            # Get the directory where the jar file is located
            jar_dir=$(dirname "$jar_file")
            
            # Create extraction directory in the same location as the jar file
            extraction_dir="$jar_dir/$jar_basename"
            mkdir -p "$extraction_dir"
            
            # Extract the JAR file
            echo "Extracting $jar_file to $extraction_dir..."
            unzip -q "$jar_file" -d "$extraction_dir"
            
            # Create subdirectories for this JAR
            mkdir -p "decompiled/cfr/$jar_basename"
            mkdir -p "decompiled/procyon/$jar_basename"
            
            echo "Decompiling $jar_file..."
            
            # Decompile using CFR
            echo "  - with CFR..."
            java -jar "$CFR_PATH" "$jar_file" --outputdir "decompiled/cfr/$jar_basename"
            
            # Decompile using Procyon
            echo "  - with Procyon..."
            java -jar "$PROCYON_PATH" "$jar_file" -o "decompiled/procyon/$jar_basename"
        done
    fi
else
    # Direct decompilation for JAR files
    # Get basename of jar file without extension for directory naming
    jar_basename=$(basename "$plugin_bundle" .jar)
    # Get the directory where the jar file is located
    jar_dir=$(dirname "$plugin_bundle")
    
    # Create extraction directory in the same location as the jar file
    extraction_dir="$jar_dir/$jar_basename"
    mkdir -p "$extraction_dir"
    
    # Extract the JAR file
    echo "Extracting $plugin_bundle to $extraction_dir..."
    unzip -q "$plugin_bundle" -d "$extraction_dir"
    
    mkdir -p "decompiled/cfr/$jar_basename"
    mkdir -p "decompiled/procyon/$jar_basename"
    
    # Decompile using CFR
    echo "Decompiling with CFR..."
    java -jar "$CFR_PATH" "$plugin_bundle" --outputdir "decompiled/cfr/$jar_basename"
    
    # Decompile using Procyon
    echo "Decompiling with Procyon..."
    java -jar "$PROCYON_PATH" "$plugin_bundle" -o "decompiled/procyon/$jar_basename"
fi

echo "Decompilation and extraction complete."
echo "CFR output: $(pwd)/decompiled/cfr"
echo "Procyon output: $(pwd)/decompiled/procyon"
echo "JAR contents extracted to directories named after each JAR file"