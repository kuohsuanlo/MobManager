/*
 * Copyright 2013 Michael McKnight. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package ninja.mcknight.bukkit.mobmanager.common.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

import ninja.mcknight.bukkit.mobmanager.limiter.util.MobType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

public class ExtendedEntityType
{
	private static int nextId = 0;
	public static final ExtendedEntityType UNKNOWN;
	private static LinkedHashMap<String, ExtendedEntityType> entityTypes = new LinkedHashMap<String, ExtendedEntityType>();

	// Adds entities
	static
	{
                // EntityTypes
		for (EntityType eType : EntityType.values())
		{
			if (eType.isAlive() && eType != EntityType.PLAYER && eType != EntityType.ARMOR_STAND)
				new ExtendedEntityType(eType, "");
		}

                // HORSE subtypes by style and color
                ExtendedEntityType horse = ExtendedEntityType.valueOf(EntityType.HORSE);
                for (Horse.Style s : Horse.Style.values()) {
			if (s == Horse.Style.NONE)
				s = null;
			for (Horse.Color c : Horse.Color.values()) {
				new ExtendedEntityType(EntityType.HORSE, new Object[] {c, s}, horse);
			}
		}

                // LLAMA subtypes by color
                ExtendedEntityType llama = ExtendedEntityType.valueOf(EntityType.LLAMA);
                for (Llama.Color c : Llama.Color.values()) {
                        new ExtendedEntityType(EntityType.LLAMA, new Object[] {c}, llama);
                }

                // OCELOT subtypes by cattype
                ExtendedEntityType ocelot = ExtendedEntityType.valueOf(EntityType.OCELOT);
                for (Ocelot.Type t : Ocelot.Type.values()) {
			new ExtendedEntityType(EntityType.OCELOT, new Object[] {t}, ocelot);
		}

                // PARROT subtypes by variant
                ExtendedEntityType parrot = ExtendedEntityType.valueOf(EntityType.PARROT);
                for (Parrot.Variant v : Parrot.Variant.values()) {
                        new ExtendedEntityType(EntityType.PARROT, new Object[] {v}, parrot);
                }

                // RABBIT subtypes by rabbittype
                ExtendedEntityType rabbit = ExtendedEntityType.valueOf(EntityType.RABBIT);
                for (Rabbit.Type t : Rabbit.Type.values()) {
			new ExtendedEntityType(EntityType.RABBIT, new Object[] {t}, rabbit);
		}

                // VILLAGER subtypes by profression
                ExtendedEntityType villager = ExtendedEntityType.valueOf(EntityType.VILLAGER);
                for (Villager.Profession p : Villager.Profession.values()) {
                        if (p != Villager.Profession.NORMAL && p != Villager.Profession.HUSK)
                                new ExtendedEntityType(EntityType.VILLAGER, new Object[] {p}, villager);
                }

		// Unknown mobs
		UNKNOWN = new ExtendedEntityType(EntityType.UNKNOWN, "");
	}

	public static ExtendedEntityType[] values()
	{
		return entityTypes.values().toArray(new ExtendedEntityType[entityTypes.size()]);
	}

	public static ExtendedEntityType valueOf(int id)
	{
		for (ExtendedEntityType type : values())
		{
			if (type.id == id)
				return type;
		}

		return null;
	}

	public static ExtendedEntityType valueOf(EntityType entityType)
	{
		return valueOf(entityType.toString());
	}

	public static ExtendedEntityType valueOf(LivingEntity entity)
	{
		return valueOf(getEntityTypeData(entity));
	}

	public static ExtendedEntityType valueOf(String string)
	{
		ExtendedEntityType type = entityTypes.get(string.toUpperCase());

		return type != null ? type : UNKNOWN;
	}

	public static String getEntityTypeData(LivingEntity entity)
	{
		String entityData = getEntityData(entity);

		if (entityData.length() != 0)
			return String.format("%s%s%s", entity.getType().toString(), getDataSeperator(), entityData);
		return entity.getType().toString();
	}

	public static String getEntityData(LivingEntity entity)
	{
                // Handle the case for horses
		if (entity.getType() == EntityType.HORSE) {
			Horse horse = (Horse) entity;
			return horse.getStyle() + getDataSeperator() + horse.getColor();
		}

                if (entity.getType() == EntityType.LLAMA) {
                        Llama llama = (Llama) entity;
                        return llama.getColor().toString();
                }

                if (entity.getType() == EntityType.OCELOT) {
                        Ocelot ocelot = (Ocelot) entity;
                        return ocelot.getCatType().toString();
                }

                if (entity.getType() == EntityType.PARROT) {
                        Parrot parrot = (Parrot) entity;
                        return parrot.getVariant().toString();
                }

                if (entity.getType() == EntityType.RABBIT) {
                        Rabbit rabbit = (Rabbit) entity;
                        return rabbit.getRabbitType().toString();
                }

                if (entity.getType() == EntityType.VILLAGER) {
                        Villager villager = (Villager) entity;
                        return villager.getProfession().toString();
                }

		return "";
	}

	private final int id;
	private final EntityType eType;
	private final Object eData;
	private final MobType mobType;
	private final ExtendedEntityType parent;

	private ExtendedEntityType(EntityType eType, Object eData)
	{
		this(eType, eData, null);
	}

	private ExtendedEntityType(EntityType eType, Object eData, ExtendedEntityType parent)
	{
		id = nextId++;
		this.eType = eType;
		this.eData = eData;
		this.parent = parent;

		if (eType != null && eType.getEntityClass() != null)
			mobType = MobType.valueOf(eType);
		else
			mobType = null;

		entityTypes.put(getTypeData().toUpperCase(), this);
	}

	public EntityType getBukkitEntityType()
	{
		return eType;
	}

	/**
	 * Returns the pre-calculated mob type for this EntityType
	 * @return The EntityTypes MobType
	 */
	public MobType getMobType()
	{
		return mobType;
	}

	/**
	 * Returns the MobType of this entity</br>
	 * If the MobType is unknown it is calculated given an Entity</br>
	 * This provides support for mobs which do not exist in Vanilla Minecraft
	 * @param entity The entity for which we want the MobType for (Should match the EntityType)
	 * @return The Entities MobType
	 */
	public MobType getMobType(LivingEntity entity)
	{
		if (mobType == null)
			return MobType.valueOf(entity);
		return mobType;
	}

	public ExtendedEntityType getParent()
	{
		return parent;
	}

	public boolean hasParent()
	{
		return parent != null;
	}

	public String getData()
	{
		if (eData instanceof Object[])
		{
			Object[] a = (Object[]) eData;
			String data = "";
			for (int i = 0; i < a.length; ++i)
			{
				if (a[i] == null)
					continue;

				if (i > 0)
					data += getDataSeperator();
				data += a[i];
			}
			return data;
		}
		return eData.toString();
	}

	public static String getDataSeperator()
	{
		return "_";
	}

	public String getTypeData()
	{
		String typeData = eType.toString();
		String dataString = getData();

		if (dataString.length() != 0)
			typeData += getDataSeperator() + dataString;

		return typeData;
	}

	public static String getExtendedEntityList(boolean subtypes)
	{
		final int charLimit = 68;
		int currentLoc = 1;

		StringBuilder list = new StringBuilder();

		for (ExtendedEntityType type : entityTypes.values())
		{
			if (subtypes && !type.hasParent() || !subtypes && type.hasParent())
				continue;

			String addition = type.getTypeData();

			if (currentLoc + addition.length() + 2 > charLimit)
			{
				currentLoc = 1;
				list.append(",\n");
			}

			if (currentLoc != 1)
				list.append(", ");
			list.append(addition);
			currentLoc += addition.length();
		}

		return list.toString();
	}

	@Override
	public String toString()
	{
		return getTypeData();
	}

	public LivingEntity spawnMob(Location loc)
	{
                // Bukkit.getServer().getLogger().info("MM spawnmob: START eType ==" + eType);
                if (eType.getEntityClass() == null || loc == null || loc.getWorld() == null)
			return null;

		LivingEntity entity = (LivingEntity) loc.getWorld().spawnEntity(loc, eType);

		if (entity == null)
			return null;

                Horse.Style hs = null;
		Horse.Color hc = null;
		Llama.Color lc = null;
                Ocelot.Type ot = null;
                Parrot.Variant pv = null;
                Rabbit.Type rt = null;
                Villager.Profession vp = null;

                //extract extra data in general...
                if (eData instanceof Object[]) {
                        Object[] arrObjData = (Object[]) eData;
                        for (Object o: arrObjData){
                                if (o == null)
                                        continue;
                                if (o instanceof Horse.Style)
                                        hs = (Horse.Style) o;
                                if (o instanceof Horse.Color)
                                        hc = (Horse.Color) o;
                                if (o instanceof Llama.Color)
                                        lc = (Llama.Color) o;
                                if (o instanceof Ocelot.Type)
                                        ot = (Ocelot.Type) o;
                                if (o instanceof Parrot.Variant)
                                        pv = (Parrot.Variant) o;
                                if (o instanceof Rabbit.Type)
                                        rt = (Rabbit.Type) o;
                                if (o instanceof Villager.Profession)
                                        vp = (Villager.Profession) o;
                        }
                }

                if (eType == EntityType.HORSE)
		{
			Horse horse = (Horse) entity;
                        if (hs == null)
				hs = RandomUtil.getRandomElement( Horse.Style.values());
                        if (hc == null)
				hc = RandomUtil.getRandomElement( Horse.Color.values());

			horse.setStyle( hs);
			horse.setColor( hc);
			return horse;
		}

                if (eType == EntityType.LLAMA)
		{
			Llama llama = (Llama) entity;
                        if (lc == null)
				lc = RandomUtil.getRandomElement( Llama.Color.values());
			llama.setColor( lc);
			return llama;
		}

                if (eType == EntityType.OCELOT)
		{
			Ocelot ocelot = (Ocelot) entity;
                        if (ot == null)
				ot = RandomUtil.getRandomElement( Ocelot.Type.values());
			ocelot.setCatType( ot);
			return ocelot;
		}

                if (eType == EntityType.PARROT)
                {
                        Parrot parrot = (Parrot) entity;
                        if (pv == null)
                                pv = RandomUtil.getRandomElement( Parrot.Variant.values());
                        parrot.setVariant( pv);
                        return parrot;
                }

                if (eType == EntityType.RABBIT)
                {
                        Rabbit rabbit = (Rabbit) entity;
                        if (rt == null)
                                rt = RandomUtil.getRandomElement( Rabbit.Type.values());
                        rabbit.setRabbitType( rt);
                        return rabbit;
                }

                ArrayList<Villager.Profession> professions = new ArrayList<Villager.Profession>();
                for (Villager.Profession p : Villager.Profession.values()) {
                        if (p != Villager.Profession.NORMAL && p != Villager.Profession.HUSK)
                                professions.add( p);
                }
                Random i = new Random();
                if (eType == EntityType.VILLAGER)
                {
                        Villager villager = (Villager) entity;
                        if (vp == null)
                                vp = professions.get(i.nextInt( professions.size()));
                        villager.setProfession( vp);
                        return villager;
                }

		switch (eType)
		{
		case SKELETON:
			entity.getEquipment().setItemInMainHand( new ItemStack( Material.BOW));
			break;
                case WITHER_SKELETON:
			entity.getEquipment().setItemInMainHand( new ItemStack( Material.STONE_SWORD));
			break;
		case PIG_ZOMBIE:
			entity.getEquipment().setItemInMainHand( new ItemStack( Material.GOLD_SWORD));
			break;
		default:
		}

		return entity;
	}

	public int ordinal()
	{
		return id;
	}

	public boolean isWide()
	{
		switch (eType)
		{
		case CAVE_SPIDER:
		case ENDER_DRAGON:
		case GHAST:
		case GIANT:
		case HORSE:
		case IRON_GOLEM:
		case MAGMA_CUBE:
		case SLIME:
		case SPIDER:
		case WITHER:
			return true;
		default:
			return false;
		}
	}

	public boolean isTall()
	{
		switch (eType)
		{
		case ENDERMAN:
		case ENDER_DRAGON:
		case GHAST:
		case GIANT:
		case HORSE:
		case IRON_GOLEM:
		case MAGMA_CUBE:
		case SLIME:
		case WITHER:
			return true;
		default:
			return false;
		}
	}
}
