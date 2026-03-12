package com.example.vivavital;

import java.util.HashMap;

public class RecipeData {
    private static final HashMap<String, Recipe> recipeMap = new HashMap<>();

    static {
        // Recipe 1: Baked Eggs with Spinach
        recipeMap.put("Baked Eggs with Spinach", new Recipe(
                "Baked Eggs with Spinach",
                "7 minutes",
                "190-220 kcal",
                R.drawable.egg_spinach,
                new String[]{
                        "2 large eggs",
                        "1 cup fresh spinach (30g)",
                        "1 tsp olive oil (or butter)",
                        "1 small garlic clove, minced",
                        "Salt & black pepper",
                        "1 tbsp grated cheese (optional)"
                },
                "1. Heat the olive oil (or butter) in a pan over medium heat.\n" +
                        "2. Add the minced garlic and sauté for a few seconds.\n" +
                        "3. Add the spinach and cook until wilted.\n" +
                        "4. Crack the eggs into the pan and scramble.\n" +
                        "5. Season with salt and pepper.\n" +
                        "6. Add cheese if desired. Serve hot!"
        ));

        // Recipe 2: Banana Pancakes
        recipeMap.put("Banana Pancakes", new Recipe(
                "Banana Pancakes",
                "10 minutes",
                "250 kcal",
                R.drawable.banana_pancakes,
                new String[]{
                        "1 ripe banana",
                        "2 eggs",
                        "1/4 tsp baking powder",
                        "Pinch of cinnamon",
                        "1 tsp olive oil (for cooking)"
                },
                "1. Mash the banana in a bowl.\n" +
                        "2. Add eggs, baking powder, and cinnamon. Mix well.\n" +
                        "3. Heat oil in a pan over medium heat.\n" +
                        "4. Pour batter to form small pancakes.\n" +
                        "5. Cook each side for 1-2 minutes until golden.\n" +
                        "6. Serve warm."
        ));

        // Recipe 3: Mediterranean Salad
        recipeMap.put("Mediterranean Salad", new Recipe(
                "Mediterranean Salad",
                "5 minutes",
                "180 kcal",
                R.drawable.mediterranean_salad,
                new String[]{
                        "1 cup cherry tomatoes, halved",
                        "1/2 cucumber, diced",
                        "1/4 cup black olives, sliced",
                        "1/4 cup feta cheese, crumbled",
                        "1 tbsp olive oil",
                        "1 tsp lemon juice",
                        "Salt & oregano to taste"
                },
                "1. In a large bowl, combine tomatoes, cucumber, olives, and feta.\n" +
                        "2. Drizzle olive oil and lemon juice on top.\n" +
                        "3. Sprinkle with salt and oregano.\n" +
                        "4. Toss gently and serve immediately."
        ));

        // Recipe 4: Tuna Sandwich
        recipeMap.put("Tuna Sandwich", new Recipe(
                "Tuna Sandwich",
                "8 minutes",
                "300 kcal",
                R.drawable.tuna_sandwich,
                new String[]{
                        "1 can tuna in water, drained",
                        "1 tbsp light mayo or Greek yogurt",
                        "1 tsp mustard (optional)",
                        "2 slices whole grain bread",
                        "Lettuce leaves",
                        "Sliced tomato",
                        "Salt & pepper to taste"
                },
                "1. In a bowl, mix tuna with mayo (or yogurt), mustard, salt, and pepper.\n" +
                        "2. Toast the bread slices if desired.\n" +
                        "3. Layer lettuce, tuna mixture, and tomato slices on one slice.\n" +
                        "4. Top with the other slice and serve."
        ));
    }

    public static Recipe getRecipeByName(String name) {
        return recipeMap.get(name);
    }
}