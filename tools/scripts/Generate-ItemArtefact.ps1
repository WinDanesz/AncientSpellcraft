

[CmdletBinding()]
param (
    [Parameter()]
    [string]
    $Item
)

###############################################
# Static
$script:srcPath = "C:\git\AncientSpellcraft\src"
$script:modid = "ancientspellcraft"
###############################################

################ Item Specific ################ 

$script:itemName = $Item

###############################################

#region Functions


function New-SimpleModelFile {
    $json = @"
{
    "parent": "item/generated",
    "textures": {
        "layer0": "$script:modid`:items/$script:itemName"
    }
}
"@

    $modelPath = "$($script:srcPath)\main\resources\assets\$($script:modid)\models\item\$($script:itemName).json"
    $json |  Out-File $modelPath -Encoding utf8 -NoNewline
    Write-Host "Generated $modelPath texture"
}

function Set-CRLF {
    [CmdletBinding()]
    Param([Parameter(ValueFromPipeline)]$string
    )   
    return $string -Replace ("`n", "`r`n")
}

function Add-LangFileEntry {
    [CmdletBinding()]
    Param([Parameter()][String]$Entry
    )

    $langFile = $script:srcPath + "\main\resources\assets\$($script:modid)\lang\en_us.lang"
    Add-Content -Path $langFile -Value $Entry
    Write-Host "Added langFile entry $Entry"
}

function Create-Texture {
    $texturePath = $script:srcPath + "\main\resources\assets\$($script:modid)\textures\items\$($script:itemName).png"
    if (!(Test-Path -Path $texturePath -PathType Leaf)) {
        $genericTexture = $script:srcPath + "\main\resources\assets\$($script:modid)\textures\items\ring_gold_generic.png"
        Copy-Item $genericTexture -Destination $texturePath
        Write-Host "Generated $texturePath texture"
    }
}

#endregion Functions

New-SimpleModelFile
Add-LangFileEntry -Entry "item.$($script:modid)\:$($script:itemName).name=TODO"
Add-LangFileEntry -Entry "item.$($script:modid)\:$($script:itemName).desc=TODO"
Create-Texture