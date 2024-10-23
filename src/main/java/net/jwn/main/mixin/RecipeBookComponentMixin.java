package net.jwn.main.mixin;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Predicate;

@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

    private String capturedSearchString;
    private ObjectSet<RecipeCollection> capturedObjectSet;

    @Inject(method = "updateCollections",
            at = @At(value = "INVOKE", target = "Ljava/util/List;removeIf(Ljava/util/function/Predicate;)Z", ordinal = 2),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void modifyRemoveIfCondition(boolean resetPageNumber, CallbackInfo ci, List<RecipeCollection> list, List<RecipeCollection> list1, String s, ClientPacketListener clientpacketlistener, ObjectSet<RecipeCollection> objectset) {
        System.out.println("Text for Search: " + s);
        this.capturedSearchString = s;  // Store the search string `s`
        this.capturedObjectSet = objectset;
        capturedObjectSet.forEach(recipeCollection -> System.out.println(recipeCollection.getRecipes()));
    }

    @Redirect(method = "updateCollections",
            at = @At(value = "INVOKE", target = "Ljava/util/List;removeIf(Ljava/util/function/Predicate;)Z", ordinal = 2))
    private boolean customRemoveIf(List<RecipeCollection> list1, Predicate<RecipeCollection> predicate) {
        // Your custom logic for removing from list1
        return list1.removeIf((p_302148_) -> {
            // Custom condition logic
            boolean success = false;
            for (RecipeHolder<?> recipeHolder : p_302148_.getRecipes()) {
                if (recipeHolder.toString().contains(capturedSearchString)) {  // Assuming 's' is accessible in scope
                    System.out.println("Searched by ID: " + p_302148_.getRecipes());
                    success = true;
                    break;
                }
            }
            return !(capturedObjectSet.contains(p_302148_) || success);
        });
    }
}
