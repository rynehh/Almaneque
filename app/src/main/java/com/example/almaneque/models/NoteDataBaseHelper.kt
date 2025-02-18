import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.almaneque.models.notedata

class NoteDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "notas.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE notas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "titulo TEXT , " +
                    "regado TEXT, " +
                    "luz TEXT, " +
                    "fertilizacion TEXT, " +
                    "clima TEXT, " +
                    "descripcion TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS notas")
        onCreate(db)
    }


    fun saveNoteIfNotExists(note: notedata) {
        val db = writableDatabase

        // Verificar si ya existe una nota con todos los campos iguales
        val cursor = db.rawQuery(
            "SELECT * FROM notas WHERE titulo = ? AND regado = ? AND luz = ? AND fertilizacion = ? AND clima = ? AND descripcion = ?",
            arrayOf(note.titulo, note.regado, note.luz, note.fertilizacion, note.clima, note.descripcion)
        )

        // Si no existe una nota exactamente igual, la inserta
        if (!cursor.moveToFirst()) {
            db.execSQL(
                "INSERT INTO notas (titulo, regado, luz, fertilizacion, clima, descripcion) VALUES (?, ?, ?, ?, ?, ?)",
                arrayOf(note.titulo, note.regado, note.luz, note.fertilizacion, note.clima, note.descripcion)
            )
        }

        cursor.close()
        db.close()
    }



    fun getNotes(): List<notedata> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM notas", null)
        val notes = mutableListOf<notedata>()

        while (cursor.moveToNext()) {
            notes.add(
                notedata(
                    id_nota = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                    regado = cursor.getString(cursor.getColumnIndexOrThrow("regado")),
                    luz = cursor.getString(cursor.getColumnIndexOrThrow("luz")),
                    fertilizacion = cursor.getString(cursor.getColumnIndexOrThrow("fertilizacion")),
                    clima = cursor.getString(cursor.getColumnIndexOrThrow("clima")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                )
            )
        }
        cursor.close()
        db.close()
        return notes
    }


    fun clearNotes() {
        val db = writableDatabase
        db.execSQL("DELETE FROM notas")
        db.close()
    }
}
