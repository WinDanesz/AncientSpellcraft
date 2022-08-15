

[CmdletBinding()]
param (
    [Parameter()]
    [string]
    $Spell,
    $Element = "magic",
    $Tier = "novice",
    $Type = "attack",
    $Cost = "10"
)

###############################################
# Static
$script:srcPath = "C:\git\AncientSpellcraft\src"
$script:modid = "ancientspellcraft"
$script:spellRegistryFile = "C:\git\AncientSpellcraft\src\main\java\com\windanesz\ancientspellcraft\registry\ASSpells.java"
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
    "tier": "$Tier",
    "element": "$Element",
    "type": "$Type",
    "cost": $Cost,
    "chargeup": 0,
    "cooldown": 20,
    "base_properties": {
    }
}
"@

    $filePath = "$($script:srcPath)\main\resources\assets\$($script:modid)\spells\$($script:spellName).json"
    if (!(Test-Path -Path $filePath)) {
        $json | Out-File $filePath -Encoding utf8 -NoNewline
        Write-Host "Generated $filePath spell json"
    }
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

        if (!(Test-Path -Path $texturePath)) {
            Copy-Item $genericTexture -Destination $texturePath
            Write-Host "Generated $texturePath texture"
        }
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


$displayName = (Get-Culture).TextInfo.ToTitleCase([String]::Join(" ", $spellName.Split("_")))
Add-LangFileEntry -Entry "spell.$($script:modid)\:$($script:spellName).desc=TODO"
Add-LangFileEntry -Entry "spell.$($script:modid)\:$($script:spellName)=$displayName"
Create-Texture
Add-SpellJsonFile

# Add member
$lineOfLastSpell = (Select-String -Path $script:spellRegistryFile -Pattern 'public\sstatic\sfinal\sSpell')[-1].LineNumber
$textToAdd = "`n    public static final Spell $($script:spellName) = placeholder();"
$fileContent = Get-Content $script:spellRegistryFile
$fileContent[$lineOfLastSpell-1] += $textToAdd
$fileContent | Set-Content $script:spellRegistryFile

# Add registry placeholder
$lineOfLastSpell = (Select-String -Path $script:spellRegistryFile -Pattern 'registry\.register\(new\s')[-1].LineNumber
$textToAdd = "`n        registry.register(new <<ADD SPELL>>);"
$fileContent = Get-Content $script:spellRegistryFile
$fileContent[$lineOfLastSpell-1] += $textToAdd
$fileContent | Set-Content $script:spellRegistryFile
