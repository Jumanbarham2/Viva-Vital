package com.example.vivavital;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Recipe_Sample extends AppCompatActivity {

    private ImageView recipeImage;
    private TextView recipeName, recipeTime, recipeCalories, recipeInstructions;
    private LinearLayout ingredientsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_sample);

        // Initialize views
        recipeImage = findViewById(R.id.recipe_image);
        recipeName = findViewById(R.id.recipe_name);
        recipeTime = findViewById(R.id.recipe_time);
        recipeCalories = findViewById(R.id.recipe_calories);
        recipeInstructions = findViewById(R.id.recipe_instructions);
        ingredientsContainer = findViewById(R.id.ingredients_container);

        // Get recipe data from intent
        String name = getIntent().getStringExtra("recipe_name");
        String calories = getIntent().getStringExtra("recipe_calories");
        String time = getIntent().getStringExtra("recipe_time");

        // Get full recipe details from RecipeData
        Recipe recipe = RecipeData.getRecipeByName(name);

        if (recipe != null) {
            // Set recipe image
            recipeImage.setImageResource(recipe.imageResId);

            // Set basic info
            recipeName.setText(recipe.name);
            recipeTime.setText("Cook Time\n" + recipe.cookTime);
            recipeCalories.setText("Calories\n" + recipe.calories);

            // Set instructions
            recipeInstructions.setText(recipe.instructions);

            // Clear any existing checkboxes
            ingredientsContainer.removeAllViews();

            // Add checkboxes for each ingredient
            for (String ingredient : recipe.ingredients) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(ingredient);
                checkBox.setTextSize(16); // Set appropriate text size
                checkBox.setPadding(8, 8, 8, 8); // Add some padding
                ingredientsContainer.addView(checkBox);
            }
        } else {
            // Fallback to intent data if recipe not found in RecipeData
            recipeName.setText(name);
            recipeTime.setText(time);
            recipeCalories.setText(calories);
        }

        // Optional: Set up back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(name);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Close this activity and return to previous
        return true;
    }
}