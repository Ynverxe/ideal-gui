rootProject.name = "ideal-gui"
include("abstract")
include("minestom-impl")
include("item")
include("minestom-impl:minestom-demo")
findProject(":minestom-impl:minestom-demo")?.name = "minestom-demo"
