

[CmdletBinding()]
param (
    [Parameter()]
    [string]
    $Spell
)

###############################################
# Static
$script:srcPath = "C:\git\AncientSpellcraft\src"
$script:modid = "ancientspellcraft"
###############################################

################ Item Specific ################ 

$script:spellName = $Spell

###############################################

#region Functions

function Add-SpellJsonFile {
    $json = @"
{
    "enabled": {
        "book": true,
        "scroll": true,
        "wands": true,
        "npcs": true,
        "dispensers": true,
        "commands": true,
        "treasure": true,
        "trades": true,
        "looting": true
    },
    "tier": "novice",
    "element": "magic",
    "type": "attack",
    "cost": 10,
    "chargeup": 0,
    "cooldown": 20,
    "base_properties": {
    }
}
"@

    $filePath = "$($script:srcPath)\main\resources\assets\$($script:modid)\spells\$($script:spellName).json"
    $json | Out-File $filePath -Encoding utf8 -NoNewline
    Write-Host "Generated $filePath spell json"
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
    $texturePath = $script:srcPath + "\main\resources\assets\$($script:modid)\textures\spells\$($script:spellName).png"
    if (!(Test-Path -Path $texturePath -PathType Leaf)) {
        $genericTexture = $script:srcPath + "\main\resources\assets\$($script:modid)\textures\spells\none.png"
        Copy-Item $genericTexture -Destination $texturePath
        Write-Host "Generated $texturePath texture"
    }
}

# function Add-SoundFileEntry {
#     [CmdletBinding()]
#     Param([Parameter()][String]$Entry
#     )

#     $soundFile = $script:srcPath + "\main\resources\assets\$($script:modid)\sounds.json"
#     $sounds = Get-Content -Path $soundFile
#     $sounds[-1] = @"
#     "spell.$($script:spellName)":                  {"category": "spells", "sounds": ["ebwizardry:buff"]},
# "@
#     $sounds += "}"
#     $sounds | Out-File $soundFile -Encoding utf8 -NoNewline
# }

#endregion Functions

Add-LangFileEntry -Entry "item.$($script:modid)\:$($script:spellName).name=TODO"
Add-LangFileEntry -Entry "item.$($script:modid)\:$($script:spellName).desc=TODO"
Create-Texture
Add-SpellJsonFile

