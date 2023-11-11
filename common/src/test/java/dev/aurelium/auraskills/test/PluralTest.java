package dev.aurelium.auraskills.test;

import org.atteo.evo.inflector.English;
import org.junit.jupiter.api.Test;

public class PluralTest {

    @Test
    public void testPlural() {
        assert English.plural("mana_ability").equals("mana_abilities");
        assert English.plural("state").equals("states");
        assert English.plural("cause").equals("causes");
        assert English.plural("excluded_cause").equals("excluded_causes");
        assert English.plural("damager").equals("damagers");
        assert English.plural("excluded_material").equals("excluded_materials");
        assert English.plural("trigger").equals("triggers");
    }

}
