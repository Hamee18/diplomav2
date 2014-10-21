package com.attila.horvath.item;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.utils.Disposable;

public class Item extends ModelInstance implements RenderableProvider, Disposable {

	private final static String[] objects = { "I", "L", "O", "T", "Z" };
	private final btCollisionObject body;
	private boolean moving = false;
	private static Random random = new Random();

	public Item(Model model, btCollisionShape shape) {
		super(model);
		body = new btCollisionObject();
		body.setCollisionShape(shape);

		setBasics();
	}

	private void setBasics() {
		this.transform.trn(0, Gdx.graphics.getHeight()/5, 0);
		this.transform.rotate(0, 1, 0, 135);
		body.setWorldTransform(this.transform);
		body.setCollisionFlags(body.getCollisionFlags()
				| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
	}

	public btCollisionObject getBody() {
		return body;
	}

	public boolean getMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public void setUserValue(int value) {
		body.setUserValue(value);
	}

	@Override
	public void dispose() {
		body.dispose();
	}

	public void setTransform(float delta) {
		this.transform.trn(0, -delta, 0);
		setWordTransform();
	}

	public void setWordTransform() {
		body.setWorldTransform(this.transform);

	}

	public static class Constructor {
		private final static AssetsManager assets = new AssetsManager();
		private Random randomColor = new Random();
		private final Color colors[] = { Color.BLUE, Color.CYAN,
				Color.DARK_GRAY, Color.GRAY, Color.GREEN, Color.LIGHT_GRAY,
				Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,
				Color.WHITE, Color.YELLOW };
		private final Model model;

		public Constructor() {
			model = assets.getAsset("obj/" + objects[random.nextInt(objects.length)] + ".g3db");
			model.getMaterial("Material").set(
					ColorAttribute.createDiffuse(colors[randomColor
							.nextInt(colors.length - 1)]));
		}

		private static btConvexHullShape createConvexHullShape(
				final Model model, boolean optimize) {
			final Mesh mesh = model.meshes.get(0);
			final btConvexHullShape shape = new btConvexHullShape(
					mesh.getVerticesBuffer(), mesh.getNumVertices(),
					mesh.getVertexSize());
			if (!optimize)
				return shape;
			// now optimize the shape
			final btShapeHull hull = new btShapeHull(shape);
			hull.buildHull(shape.getMargin());
			final btConvexHullShape result = new btConvexHullShape(hull);
			// delete the temporary shape
			shape.dispose();
			hull.dispose();
			return result;
		}

		public Item construct() {
			return new Item(model, createConvexHullShape(model, true));
		}
	}
}
