package com.example.deckbuilder.DeckBuilder;

import com.example.deckbuilder.DeckBuilder.model.ships.Ship;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.List;

public class FightModel {

    List<Ship> fightingShips = Lists.newArrayList();
    Multimap<Ship, Ship> blockers = ArrayListMultimap.create();

    public void addShip(Ship ship) {
        fightingShips.add(ship);
    }

    public boolean addBlocker(Ship ship, Ship shipToBlock) {
        blockers.put(shipToBlock, ship);
        //TODO if the target ship has any features meaning it can't be blocked, this should return false.
        return true;
    }
}
