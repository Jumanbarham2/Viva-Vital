package com.example.vivavital;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Recipe_Results extends AppCompatActivity {
    private ImageView recipe1Image, recipe2Image, recipe3Image, recipe4Image;
    private TextView recipe1Name, recipe2Name, recipe3Name, recipe4Name;
    private TextView recipe1Time, recipe2Time, recipe3Time, recipe4Time;
    private TextView recipe1Calorie, recipe2Calorie, recipe3Calorie, recipe4Calorie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_results);

        // Get selections
        Intent intent = getIntent();
        boolean allergyEggs = intent.getBooleanExtra("allergy_eggs", false);
        boolean allergyFish = intent.getBooleanExtra("allergy_fish", false);
        boolean allergyDairy = intent.getBooleanExtra("allergy_dairy", false);
        boolean allergyNuts = intent.getBooleanExtra("allergy_nuts", false);
        boolean allergyGluten = intent.getBooleanExtra("allergy_gluten", false);
        boolean allergyShellfish = intent.getBooleanExtra("allergy_shellfish", false);

        String dietPreference = intent.getStringExtra("prefer");
        String mealPreference = intent.getStringExtra("meal_preference");

        initializeViews();
        applyFilters(allergyEggs, allergyFish, allergyDairy, allergyNuts, allergyGluten, allergyShellfish,
                dietPreference, mealPreference);
        setupClickListeners();
    }

    private void initializeViews() {
        recipe1Image = findViewById(R.id.recipe1_image);
        recipe1Name = findViewById(R.id.recipe1_name);
        recipe1Time = findViewById(R.id.recipe1_time);
        recipe1Calorie = findViewById(R.id.recipe1_calories);

        recipe2Image = findViewById(R.id.recipe2_image);
        recipe2Name = findViewById(R.id.recipe2_name);
        recipe2Time = findViewById(R.id.recipe2_time);
        recipe2Calorie = findViewById(R.id.recipe2_calories);

        recipe3Image = findViewById(R.id.recipe3_image);
        recipe3Name = findViewById(R.id.recipe3_name);
        recipe3Time = findViewById(R.id.recipe3_time);
        recipe3Calorie = findViewById(R.id.recipe3_calories);

        recipe4Image = findViewById(R.id.recipe4_image);
        recipe4Name = findViewById(R.id.recipe4_name);
        recipe4Time = findViewById(R.id.recipe4_time);
        recipe4Calorie = findViewById(R.id.recipe4_calories);
    }

    private void applyFilters(boolean allergyEggs, boolean allergyFish, boolean allergyDairy,
                              boolean allergyNuts, boolean allergyGluten, boolean allergyShellfish,
                              String dietPreference, String mealPreference) {
        // Allergies
        if (allergyEggs) {
            hideRecipe(recipe1Image, recipe1Name, recipe1Time, recipe1Calorie);
        }
        if (allergyFish) {
            hideRecipe(recipe4Image, recipe4Name, recipe4Time, recipe4Calorie);
        }
        if (allergyDairy) {
            hideRecipe(recipe1Image, recipe1Name, recipe1Time, recipe1Calorie);
            hideRecipe(recipe2Image, recipe2Name, recipe2Time, recipe2Calorie);
        }
        if (allergyNuts) {
            hideRecipe(recipe2Image, recipe2Name, recipe2Time, recipe2Calorie);
        }

        // Diet preferences
        if ("Vegetarian".equals(dietPreference)) {
            hideRecipe(recipe4Image, recipe4Name, recipe4Time, recipe4Calorie);
        } else if ("Vegan".equals(dietPreference)) {
            hideRecipe(recipe1Image, recipe1Name, recipe1Time, recipe1Calorie);
            hideRecipe(recipe4Image, recipe4Name, recipe4Time, recipe4Calorie);
        }

        // Meal preferences
        if ("Low-calorie".equals(mealPreference)) {
            hideRecipe(recipe2Image, recipe2Name, recipe2Time, recipe2Calorie);
        }
    }

    private void hideRecipe(ImageView imageView, TextView name, TextView time, TextView calorie) {
        imageView.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        time.setVisibility(View.GONE);
        calorie.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        setClickListener(recipe1Image, recipe1Name, "Baked Eggs with Spinach");
        setClickListener(recipe2Image, recipe2Name, "Banana Pancakes");
        setClickListener(recipe3Image, recipe3Name, "Mediterranean Salad");
        setClickListener(recipe4Image, recipe4Name, "Tuna Sandwich");
    }

    private void setClickListener(ImageView image, TextView text, String recipeName) {
        View.OnClickListener listener = v -> openRecipeDetail(recipeName);
        image.setOnClickListener(listener);
        text.setOnClickListener(listener);
    }

    private void openRecipeDetail(String name) {
        Intent intent = new Intent(this, Recipe_Sample.class);
        intent.putExtra("recipe_name", name);
        startActivity(intent);
    }
}
