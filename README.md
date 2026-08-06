# Moodleaf

A little journaling app for Android, built with Kotlin + Jetpack Compose.

Each entry is a freeform page: pick a mood, drag photos and sticky notes
around, drop in a sticker, tag it with the day's weather. Browse past
entries, see them on a wall calendar, and watch Trends show how mood shifts
over time, by weekday, and by weather.

## Running it

1. Open the folder in Android Studio, let it sync.
2. Device Manager → create a virtual device, or plug in a phone with USB
   debugging enabled.
3. Run the `app` configuration.

## Project structure

```
app/src/main/java/com/cajsa/moodleaf/
├── model/        Domain types (Mood, JournalEntry, PageElement, Weather)
├── data/         Local storage, weather fetching, repositories
├── ui/
│   ├── home/       Journal list
│   ├── editor/     The freeform page canvas
│   ├── calendar/   Wall-calendar view
│   ├── trends/     Mood charts
│   └── settings/   Theme + weather city
```

## Next steps

See `todo.md` for the backlog.
